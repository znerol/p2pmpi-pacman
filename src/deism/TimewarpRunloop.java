package deism;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Timewarp implementation of EventLoop
 * 
 * This EventRunloop implementation respects the simtime property of the events
 * supplied by the event source such that if an event for a future point in time
 * is received, execution is delayed adequately.
 * 
 * FastForwardRunloop verifies that events delivered by the event source arrive
 * in the proper order i.e. with increasing timestamps. Otherwise an
 * EventSourceOrderException is thrown.
 */
public class TimewarpRunloop implements EventRunloop {
    private boolean stop = false;
    private ExecutionGovernor governor;
    private EventMatcher terminationCondition;
    private EventMatcher snapshotCondition;
    private SortedSet<Long> snapshots;
    private Iterable<StateHistory<Long>> stateObjects;
    private long lastsimtime = 0;

    public TimewarpRunloop(ExecutionGovernor governor,
            EventMatcher terminationCondition,
            EventMatcher snapshotCondition,
            Iterable<StateHistory<Long>> stateObjects) {
        this.governor = governor;
        this.terminationCondition = terminationCondition;
        this.snapshotCondition = snapshotCondition;
        this.stateObjects = stateObjects;
        this.snapshots = new TreeSet<Long>();
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
    public void run(EventSource source, EventDispatcher disp) {
        long lastdispatchtime = lastsimtime;
                
        // take an initial snapshot
        for (StateHistory<Long> s : stateObjects) {
            s.save(lastsimtime);
        }
        snapshots.add(lastsimtime);
        
        while (!stop) {
            source.compute(lastsimtime);
            
            Event peekEvent = source.peek();

            if (terminationCondition.match(peekEvent)) {
                break;
            }

            /*
             * Suspend execution until its time to handle the event.
             */
            long newSimtime;
            if (peekEvent != null) {
                newSimtime = governor.suspendUntil(peekEvent.getSimtime());
            }
            else {
                newSimtime = governor.suspend();
            }

            lastsimtime = newSimtime;

            if (peekEvent == null || newSimtime < peekEvent.getSimtime()) {
                // Restart and reevaluate loop conditions and current event
                // when the current simulation time is less than that of the
                // next event.
                continue;
            }

            if (lastdispatchtime > lastsimtime) {
                // rollback to the last good state before timestamp of
                // peekEvent.
                
                snapshots.add(lastsimtime);
                snapshots.tailSet(lastsimtime).clear();
                Long snapshotKey = snapshots.last();
                for (StateHistory<Long> s : stateObjects) {
                    s.rollback(snapshotKey);
                }
                lastdispatchtime = lastsimtime;
                continue;
            }
            
            if (snapshotCondition.match(peekEvent)) {
                for (StateHistory<Long> s : stateObjects) {
                    s.save(lastsimtime);
                }
                snapshots.add(lastsimtime);
            }

            /*
             * This is moderately ugly. We have to remove the peek event and we
             * really want to be sure that this was actually the same like the
             * one we peeked before. Otherwise it could indicate a bug in the
             * EventSource or some concurrency issue.
             */
            Event polledEvent = source.poll();
            assert peekEvent == polledEvent;

            disp.dispatchEvent(polledEvent);
            lastdispatchtime = lastsimtime;
        }
    }

    @Override
    public void wakeup() {
        governor.resume();
    }

    @Override
    public void stop() {
        stop = true;
        wakeup();
    }
}
