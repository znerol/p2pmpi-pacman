package deism.ipc.async;

import org.apache.log4j.Logger;

import deism.ipc.base.Handler;

public class ReceiveThread<T> extends Thread {
    private final Handler<T> messageHandler;
    private final BlockingReceiveOperation<T> receiveOperation;
    private final ThreadListener threadListener;
    private boolean done = false;

    private final static Logger logger = Logger
            .getLogger(ReceiveThread.class);

    public ReceiveThread(BlockingReceiveOperation<T> receiveOperation,
            Handler<T> messageHandler) {
        this(receiveOperation, messageHandler, null);
    }

    public ReceiveThread(BlockingReceiveOperation<T> receiveOperation,
            Handler<T> messageHandler,
            ThreadListener threadListener) {
        this.receiveOperation = receiveOperation;
        this.messageHandler = messageHandler;
        this.threadListener = threadListener;
    }

    @Override
    public void run() {
        logger.debug("Start worker thread");
        if (threadListener != null) {
            threadListener.started();
        }

        while (true) {
            synchronized (this) {
                if (done) {
                    break;
                }
            }

            T item;
            try {
                item = receiveOperation.receive();
            }
            catch (InterruptedException ex) {
                continue;
            }

            if (item != null) {
                logger.debug("Received item from original queue " + item);
                messageHandler.handle(item);
            }
        }

        if (threadListener != null) {
            threadListener.stopped();
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
