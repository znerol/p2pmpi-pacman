package deism.ipc.async;

public interface BlockingReceiveOperation<T> {
    public T receive() throws InterruptedException;
}
