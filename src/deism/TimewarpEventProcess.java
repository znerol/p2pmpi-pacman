package deism;

import java.util.List;

public interface TimewarpEventProcess extends EventProcess {
    public List<StateHistory<Long>> getStateManagedObjects();
}
