package deism.ipc.base;

public interface Handler<T> {
    public void handle(T item);
}
