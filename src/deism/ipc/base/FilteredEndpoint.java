package deism.ipc.base;

/**
 * Adapter class which sends only the messages matching a given {@link
 * deism.ipc.base.Condition} to the adapted {@link deism.core.Endpoint}.
 *
 * @param <T> Type of message
 */
public class FilteredEndpoint<T> implements Endpoint<T> {
    private final Endpoint<T> endpoint;
    private final Condition<T> condition;

    public FilteredEndpoint(Endpoint<T> endpoint, Condition<T> condition) {
        this.endpoint = endpoint;
        this.condition = condition;
    }

    @Override
    public void send(T item) {
        if (condition.match(item)) {
            endpoint.send(item);
        }
    }
}
