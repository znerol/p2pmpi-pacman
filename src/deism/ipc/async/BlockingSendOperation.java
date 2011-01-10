package deism.ipc.async;

public interface BlockingSendOperation<T> {
    public void send(T item) throws InterruptedException;
}
