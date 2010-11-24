package deism;

public interface TimewarpEventSource extends EventSource, StateHistory<Long> {
    boolean pendingEventsAvailable();
}
