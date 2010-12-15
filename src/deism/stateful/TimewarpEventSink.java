package deism.stateful;

import deism.core.EventSink;

public interface TimewarpEventSink extends EventSink, StateHistory<Long> {

}
