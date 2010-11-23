package deism;

public interface TimewarpEventSource<K> extends EventSource, StateHistory<K> {
    boolean pendingEventsAvailable();
}
