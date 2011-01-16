package deism.ipc.base;

/**
 * Adapter class which passes only the messages matching a given {@link
 * deism.ipc.base.Condition} to the adapted {@link deism.ipc.base.Handler}.
 *
 * @param <T> Type of message
 */
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
