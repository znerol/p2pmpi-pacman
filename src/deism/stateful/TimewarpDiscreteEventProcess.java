package deism.stateful;

import deism.run.DiscreteEventProcess;

public interface TimewarpDiscreteEventProcess extends DiscreteEventProcess,
        StateHistory<Long> {

}
