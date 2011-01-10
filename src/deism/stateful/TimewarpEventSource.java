package deism.stateful;

import deism.core.EventSource;

public interface TimewarpEventSource extends EventSource, StateHistory<Long> {
}
