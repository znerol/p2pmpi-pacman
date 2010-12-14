package deism;

import java.util.ArrayDeque;
import java.util.Queue;

import deism.Event;
import deism.EventSink;

public class ThreadedEventSinkRunner implements EventSink {

    private final Queue<Event> events = new ArrayDeque<Event>();
    private final Worker worker = new Worker();
    private final EventSink sink;
    private long startSimtime;

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
    public void stop() {
        synchronized (worker) {
            worker.terminate();
        }
    }

    private class Worker extends Thread {
        private boolean done = false;

        @Override
        public void run() {
            sink.start(startSimtime);

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
                    sink.offer(event);
                }
            }

            sink.stop();
        }

        public void terminate() {
            done = true;

            if (isAlive()) {
                interrupt();
            }
        }
    }
}
