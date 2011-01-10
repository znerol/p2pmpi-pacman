package deism.ipc.async;

import java.util.ArrayDeque;
import java.util.Queue;

import org.apache.log4j.Logger;

public class SendThread<T> extends Thread {

    private final Queue<T> buffer;
    private final BlockingSendOperation<T> sendOperation;
    private final ThreadListener workerListener;
    private boolean done = false;
    private final static Logger logger = Logger.getLogger(SendThread.class);

    public SendThread(BlockingSendOperation<T> sendOperation) {
        this(new ArrayDeque<T>(), sendOperation, null);
    }

    public SendThread(Queue<T> buffer,
            BlockingSendOperation<T> sendOperation) {
        this(buffer, sendOperation, null);
    }

    public SendThread(Queue<T> buffer,
            BlockingSendOperation<T> sendOperation,
            ThreadListener workerListener) {
        this.buffer = buffer;
        this.sendOperation = sendOperation;
        this.workerListener = workerListener;
    }

    public synchronized void send(T item) {
        buffer.offer(item);
        notify();
    }

    @Override
    public void run() {
        logger.debug("Start worker thread");
        if (workerListener != null) {
            workerListener.started();
        }

        while (true) {
            T item = null;
            synchronized (this) {
                if (done) {
                    break;
                }

                while ((item = buffer.poll()) == null) {
                    try {
                        this.wait();
                    }
                    catch (InterruptedException e) {
                        assert (item == null);
                        break;
                    }
                }
            }

            if (item != null) {
                logger.debug("Offering item to original queue " + item);
                try {
                    sendOperation.send(item);
                }
                catch (InterruptedException e) {
                    // ignore and continue loop
                }
            }
        }

        if (workerListener != null) {
            workerListener.stopped();
        }
        logger.debug("Terminated worker thread");
    }

    public void terminate() {
        synchronized (this) {
            done = true;
        }

        if (isAlive()) {
            logger.debug("Terminating worker thread");
            interrupt();
        }
    }
}