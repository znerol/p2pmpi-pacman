package deism.stateful;

import deism.process.DiscreteEventProcess;

public interface TimewarpDiscreteEventProcess extends DiscreteEventProcess,
        StateHistory<Long> {

}
