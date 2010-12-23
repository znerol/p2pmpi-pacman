package deism.run;

import org.apache.log4j.Logger;

import deism.core.Event;
import deism.core.EventCondition;
import deism.core.Flushable;
import deism.core.Startable;
import deism.ipc.base.Handler;
import deism.ipc.base.Message;
import deism.process.DiscreteEventProcess;

/**
 * Simple realtime implementation of EventLoop
 * 
 * This EventRunloop implementation respects the simtime property of the events
 * supplied by the event source such that if an event for a future point in time
 * is received, execution is delayed adequately.
 */
public class Runloop {
    private boolean stop = false;
    private EventCondition terminationCondition = null;
    private ExecutionGovernor governor;
    private long currentSimtime = 0;
    private StateController stateController;
    private EventCondition snapshotCondition;
    private Handler<Message> ipcHandler;
    private IpcEndpoint ipcEndpoint;
    private LvtListener lvtListener;
    private final static Logger logger = Logger.getLogger(Runloop.class);

    public Runloop(ExecutionGovernor governor,
            EventCondition terminationCondition,
            StateController stateController, EventCondition snapshotCondition,
            IpcEndpoint ipcEndpoint, Handler<Message> ipcHandler,
            LvtListener lvtListener) {
        this.governor = governor;
        this.terminationCondition = terminationCondition;
        this.stateController = stateController;
        this.snapshotCondition = snapshotCondition;
        this.ipcHandler = ipcHandler;
        this.ipcEndpoint = ipcEndpoint;
        this.lvtListener = lvtListener;
    }

    /**
     * Wait for Event timestamp
     * 
     * Suspend execution until the events timestamp has been reached. if the
     * events timestamp lies in the past, the method returns immediately.
     * Suspend indefinitely if event is null.
     * 
     * Use wakeup to resume before the timeout is reached.
     * 
     * @param e
     * @throws InterruptedException
     */
    public void run(DiscreteEventProcess process) {
        logger.debug("Start runloop at simulation time: " + currentSimtime);

        long lastSimtime = currentSimtime;
        long maxSimtime = currentSimtime;

        // support rollback to before very first event
        stateController.save(currentSimtime - 1);

        logger.debug("Start governor, source, sink");
        governor.start(currentSimtime);
        if (process instanceof Startable) {
            ((Startable) process).start(currentSimtime);
        }

        // main runloop
        while (!stop) {
            logger.debug("Begin runloop cycle");

            // fetch and handle system messages
            Message message;
            while ((message = ipcEndpoint.poll()) != null) {
                logger.info("Handle system message " + message);
                ipcHandler.handle(message);
            }

            // identify simulation event with the smallest timestamp
            Event peekEvent = process.peek(currentSimtime);
            logger.info("Optained peekEvent from event source " + peekEvent);

            // terminate runloop if event matches termination condition
            if (terminationCondition.match(peekEvent)) {
                logger.info("Event matches termination condition, exit runloop");
                break;
            }

            /*
             * Suspend execution until its time to handle the event.
             */
            long newSimtime;
            if (peekEvent != null) {
                // Notify sink that we're about to handle peekEvent
                logger.info("Announce peekEvent to event sink " + peekEvent);
                process.offer(peekEvent);

                if (process instanceof Flushable
                        && peekEvent.getSimtime() > maxSimtime) {
                    logger.debug("Flush");
                    ((Flushable) process).flush(peekEvent.getSimtime());
                }

                logger.debug("Suspend runloop until " + peekEvent.getSimtime());
                newSimtime = governor.suspendUntil(peekEvent.getSimtime());
            }
            else {
                if (process instanceof Flushable) {
                    logger.debug("Flush");
                    ((Flushable) process).flush(Long.MAX_VALUE);
                }

                logger.debug("Suspend runloop indefinitely");
                newSimtime = governor.suspend();
            }
            logger.debug("Resumed runloop at " + newSimtime);

            currentSimtime = newSimtime;

            if (peekEvent == null) {
                continue;
            }

            if (newSimtime < peekEvent.getSimtime()) {
                // Restart and reevaluate loop conditions and current event
                // when the current simulation time is less than that of the
                // next event.
                logger.debug("Resumed runloop before the expected time, restart runloop cycle");
                process.offer(peekEvent.inverseEvent());
                continue;
            }

            if (currentSimtime < lastSimtime || peekEvent.isAntimessage()) {
                if (peekEvent.isAntimessage()) {
                    process.remove(peekEvent);
                }

                logger.debug("Initiate rollback caused by peekEvent "
                        + peekEvent);
                stateController.rollback(currentSimtime);
                lastSimtime = currentSimtime;
                logger.debug("Restart runloop cycle at " + currentSimtime);
                continue;
            }

            logger.debug("Accept peekEvent " + peekEvent);
            process.remove(peekEvent);

            // Antimessages aren't allowed here.
            assert (peekEvent.isAntimessage() == false);
            logger.info("Dispatch peekEvent " + peekEvent);
            process.dispatchEvent(peekEvent);

            if (snapshotCondition.match(peekEvent)) {
                logger.info("Take snapshot at " + currentSimtime);
                stateController.save(currentSimtime);
            }

            lastSimtime = currentSimtime;
            maxSimtime = Math.max(maxSimtime, currentSimtime);

            lvtListener.update(currentSimtime);
        }

        logger.debug("Stop source, sink, governor");
        if (process instanceof Startable) {
            ((Startable) process).stop(currentSimtime);
        }
        governor.stop(currentSimtime);

        logger.info("End runloop");
    }

    public void stop() {
        stop = true;
        governor.resume();
    }
}
