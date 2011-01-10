package deism.ipc.base;

public class FilteredHandler<T> implements Handler<T> {
    private final Handler<T> handler;
    private final Condition<T> condition;

    public FilteredHandler(Handler<T> handler, Condition<T> condition) {
        this.handler = handler;
        this.condition = condition;
    }

    @Override
    public void handle(T item) {
        if (condition.match(item)) {
            handler.handle(item);
        }
    }
}
