package deism;

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
    private EventMatcher terminationCondition = null;
    private EventTimer timer;
    private long lastsimtime = 0;

    public FastForwardRunloop(EventTimer timer,
            EventMatcher terminationCondition) {
        this.timer = timer;
        this.terminationCondition = terminationCondition;
    }

    /**
     * Wait for Event timestamp
     * 
     * Suspend execution until the events timestamp has been reached. if the
     * events timestamp lies in the past, the method returns immediately.
     * Suspend indefinitely if event is null.
     * 
     * Use wakeup() to resume before the timeout is reached.
     * 
     * @param e
     * @throws InterruptedException
     */

    @Override
    public void run(EventSource source, EventDispatcher disp)
            throws EventSourceOrderException {
        while (!stop) {
            source.compute(lastsimtime);
            
            Event peekEvent = source.peek();

            if (terminationCondition.match(peekEvent)) {
                break;
            }

            /*
             * Suspend execution until its time to handle the event.
             */
            long newSimtime = timer.waitForEvent(peekEvent);
            
            if (lastsimtime > newSimtime) {
                throw new EventSourceOrderException(
                        "Event source returns events out of sequence");
            }
            
            lastsimtime = newSimtime;
            
            if (peekEvent == null || newSimtime < peekEvent.getSimtime()) {
                // Restart and reevaluate loop conditions and current event
                // when the current simulation time is less than that of the
                // next event.
                continue;
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
        }
    }

    @Override
    public void wakeup() {
        timer.wakeup();
    }

    @Override
    public void stop() {
        stop = true;
        wakeup();
    }
}
