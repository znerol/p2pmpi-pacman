package deism.process;

import deism.core.EventDispatcher;
import deism.core.EventSink;
import deism.core.EventSource;

/**
 * A logical discrete event simulation process combining the essential building
 * blocks
 */
public interface DiscreteEventProcess extends EventSource, EventSink,
        EventDispatcher {
}
