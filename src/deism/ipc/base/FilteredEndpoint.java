package deism.ipc.base;

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
