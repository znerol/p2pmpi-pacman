package deism.legacy;

import java.util.ArrayDeque;
import java.util.Queue;

import org.apache.log4j.Logger;

import deism.core.Event;
import deism.core.EventSink;
import deism.core.Startable;

public class ThreadedEventSinkRunner implements EventSink, Startable {

    private final Queue<Event> events = new ArrayDeque<Event>();
    private final Worker worker = new Worker();
    private final EventSink sink;
    private long startSimtime;
    private final static Logger logger = Logger.getLogger(ThreadedEventSinkRunner.class);

    public ThreadedEventSinkRunner(EventSink sink) {
        this.sink = sink;
    }

    @Override
    public void offer(Event event) {
        synchronized (worker) {
            events.offer(event);
            worker.notify();
        }
    }

    @Override
    public void start(long startSimtime) {
        synchronized (worker) {
            this.startSimtime = startSimtime;
            worker.start();
        }
    }

    @Override
    public void stop(long simtime) {
        synchronized (worker) {
            worker.terminate();
        }
    }

    private class Worker extends Thread {
        private boolean done = false;

        @Override
        public void run() {
            logger.debug("Start worker thread");
            if (sink instanceof Startable) {
                ((Startable)sink).start(startSimtime);
            }

            while (!done) {
                Event event = null;
                synchronized (this) {
                    for (event = events.poll(); event == null && !done;
                            event = events.poll()) {
                        try {
                            this.wait();
                        }
                        catch (InterruptedException e) {
                            // ignore and just retry
                        }
                    }
                }

                if (event != null) {
                    logger.debug("Offering event to original source " + event);
                    sink.offer(event);
                }
            }

            if (sink instanceof Startable) {
                ((Startable)sink).stop(startSimtime);
            }
            logger.debug("Terminated worker thread");
       }

        public void terminate() {
            done = true;

            if (isAlive()) {
                interrupt();
            }
        }
    }
}
