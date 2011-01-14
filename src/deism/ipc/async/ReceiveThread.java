package deism.ipc.async;

import org.apache.log4j.Logger;

import deism.ipc.base.Emitter;
import deism.ipc.base.Endpoint;

/**
 * Thread which constantly polls the given
 * {@link deism.ipc.async.BlockingReceiveOperation} sending incoming messages to
 * the specified {@link deism.ipc.base.Endpoint}
 * 
 * @param <T> Type of message
 */
public class ReceiveThread<T> extends Thread implements Emitter<T> {
    private Endpoint<T> endpoint;
    private final BlockingReceiveOperation<T> receiveOperation;
    private boolean done = false;

    private final static Logger logger = Logger.getLogger(ReceiveThread.class);

    public ReceiveThread(BlockingReceiveOperation<T> receiveOperation) {
        this.receiveOperation = receiveOperation;
    }

    @Override
    public void run() {
        logger.debug("Start worker thread");

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
                endpoint.send(item);
            }
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

    @Override
    public Endpoint<T> getEndpoint(Class<T> clazz) {
        return endpoint;
    }

    @Override
    public void setEndpoint(Endpoint<T> endpoint) {
        this.endpoint = endpoint;
    }
}
