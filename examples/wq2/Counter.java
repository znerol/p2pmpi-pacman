package wq2;

import java.util.List;

import deism.core.Event;
import deism.core.EventDispatcher;
import deism.core.EventSource;
import deism.stateful.AbstractStateHistory;

public class Counter extends AbstractStateHistory<Long, Counter.CounterState>
        implements EventDispatcher, EventSource {

    public class CounterState {
        public final CounterAvailableEvent event;
        public final boolean dispatching;

        public CounterState(CounterAvailableEvent event, boolean disp) {
            this.event = event;
            this.dispatching = disp;
        }
    }

    private CounterState currentState = new CounterState(
            new CounterAvailableEvent(0, this), false);

    @Override
    public Event peek(long currentSimtime) {
        if (currentState.dispatching == false) {
            return currentState.event;
        }
        else {
            return null;
        }
    }

    @Override
    public void remove(Event event) {
        currentState = new CounterState(currentState.event, true);
        pushHistory(currentState);
    }

    @Override
    public void dispatchEvent(Event event) {
        if (event instanceof CounterServiceEvent) {
            CounterServiceEvent cse = (CounterServiceEvent) event;
            if (cse.counterAvailableEvent.counter == this
                    && currentState.dispatching == true) {
                assert (currentState.event == cse.counterAvailableEvent);
                currentState = new CounterState(
                        createCounterAvailableEvent(cse), false);
                pushHistory(currentState);
            }
        }
    }

    @Override
    public void revertHistory(List<CounterState> tail) {
        if (tail.size() > 0) {
            currentState = tail.get(0);
        }
    }

    private CounterAvailableEvent createCounterAvailableEvent(
            CounterServiceEvent cse) {
        if (cse == null) {
            return null;
        }
        else {
            long endTime = cse.getSimtime()
                    + cse.clientArrivedEvent.getServiceTime();
            return new CounterAvailableEvent(endTime, this);
        }
    }
}
