package deism.run;

import org.apache.log4j.Logger;

import deism.core.Event;
import deism.core.EventCondition;

/**
 * Simple realtime implementation of EventLoop
 * 
 * This EventRunloop implementation respects the simtime property of the events
 * supplied by the event source such that if an event for a future point in time
 * is received, execution is delayed adequately.
 * 
 * FastForwardRunloop verifies that events delivered by the event source arrive
 * in the proper order i.e. with increasing timestamps. Otherwise an
 * EventSourceOrderException is thrown.
 */
public class FastForwardRunloop implements EventRunloop {
    private boolean stop = false;
    private EventCondition terminationCondition = null;
    private ExecutionGovernor governor;
    private long currentSimtime = 0;
    private EventRunloopRecoveryStrategy recoveryStrategy;
    private EventCondition snapshotCondition;
    private final static Logger logger = Logger.getLogger(FastForwardRunloop.class);

    public FastForwardRunloop(ExecutionGovernor governor,
            EventCondition terminationCondition,
            EventRunloopRecoveryStrategy recoveryStrategy,
            EventCondition snapshotCondition) {
        this.governor = governor;
        this.terminationCondition = terminationCondition;
        this.recoveryStrategy = recoveryStrategy;
        this.snapshotCondition = snapshotCondition;
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

    @Override
    public void run(DiscreteEventProcess process) {
        logger.debug("Start runloop at simulation time: " + currentSimtime);

        long lastSimtime = currentSimtime;
        
        // support rollback to before very first event
        recoveryStrategy.save(currentSimtime - 1);

        logger.debug("Start governor, source, sink");
        governor.start(currentSimtime);
        if (process instanceof Startable) {
            ((Startable)process).start(currentSimtime);
        }

        while (!stop) {
            logger.debug("Begin runloop cycle");
            Event peekEvent = process.peek(currentSimtime);
            logger.info("Optained peekEvent from event source " + peekEvent);

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
                logger.debug("Suspend runloop until " + peekEvent.getSimtime());
                newSimtime = governor.suspendUntil(peekEvent.getSimtime());
            }
            else {
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

                logger.debug("Initiate rollback caused by peekEvent " + peekEvent);
                recoveryStrategy.rollback(currentSimtime);
                lastSimtime = currentSimtime;
                logger.debug("Restart runloop cycle at " + currentSimtime);
                continue;
            }

            logger.debug("Accept peekEvent " + peekEvent);
            process.remove(peekEvent);

            // Antimessages aren't allowed here.
            assert(peekEvent.isAntimessage() == false);
            logger.info("Dispatch peekEvent " + peekEvent);
            process.dispatchEvent(peekEvent);

            if (snapshotCondition.match(peekEvent)) {
                logger.info("Take snapshot at " + currentSimtime);
                recoveryStrategy.save(currentSimtime);
            }

            lastSimtime = currentSimtime;
        }

        logger.debug("Stop source, sink, governor");
        if (process instanceof Startable) {
            ((Startable)process).stop(currentSimtime);
        }
        governor.stop(currentSimtime);

        logger.info("End runloop");
    }

    @Override
    public void stop() {
        stop = true;
        governor.resume();
    }
}
