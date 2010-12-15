package deism.run;

import java.util.ArrayDeque;
import java.util.Queue;

import org.apache.log4j.Logger;

import deism.core.Event;
import deism.core.EventSource;

public class ThreadedEventSourceRunner implements EventSource {
    private final Queue<Event> events = new ArrayDeque<Event>();
    private final EventSource source;
    private final ExecutionGovernor governor;
    private final Worker worker = new Worker();
    private final static Logger logger = Logger.getLogger(ThreadedEventSourceRunner.class);
    long startSimtime;
    long currentSimtime;

    public ThreadedEventSourceRunner(ExecutionGovernor governor, EventSource source) {
        this.source = source;
        this.governor = governor;
    }

    @Override
    public void start(long startSimtime) {
        synchronized (worker) {
            this.startSimtime = startSimtime;
            worker.start();
        }
    }

    @Override
    public void stop() {
        synchronized (worker) {
            worker.terminate();
        }
    }

    @Override
    public Event peek(long currentSimtime) {
        Event result = null;

        synchronized (worker) {
            this.currentSimtime = currentSimtime;
            result = events.peek();
        }

        return result;
    }

    @Override
    public void remove(Event event) {
        synchronized (worker) {
            events.remove(event);
        }
    }

    private class Worker extends Thread {
        private boolean done = false;

        @Override
        public void run() {
            logger.debug("Start worker thread");
            source.start(startSimtime);
            long mySimtime = startSimtime;

            while (!done) {
                synchronized(this) {
                    mySimtime = currentSimtime;
                }

                Event event = source.peek(mySimtime);
                logger.debug("Received event from original source " + event);

                if (event != null) {
                    source.remove(event);
                    synchronized (this) {
                        events.offer(event);
                    }
                    governor.resume(event.getSimtime());
                }
            }

            source.stop();
            logger.debug("Terminated worker thread");
        }

        public void terminate() {
            done = true;

            if (isAlive()) {
                logger.debug("Terminating worker thread");
                interrupt();
            }
        }
    }
}
