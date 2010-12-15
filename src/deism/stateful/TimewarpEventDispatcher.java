package deism.stateful;

import deism.core.EventDispatcher;

public interface TimewarpEventDispatcher extends EventDispatcher,
        StateHistory<Long> {

}
