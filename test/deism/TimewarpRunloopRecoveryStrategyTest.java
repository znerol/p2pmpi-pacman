package deism;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import deism.run.TimewarpRunloopRecoveryStrategy;
import deism.stateful.StateHistory;
import deism.stateful.StateHistoryException;
import deism.stateful.TimewarpEventSource;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TimewarpRunloopRecoveryStrategyTest {
    @Mock TimewarpEventSource eventSource;
    List<StateHistory<Long>> stateObjects;
    TimewarpRunloopRecoveryStrategy strategy;

    @Before
    public void setUp() {
        List<StateHistory<Long>> stateObjects =
            new ArrayList<StateHistory<Long>>();
        stateObjects.add(eventSource);        
        strategy = new TimewarpRunloopRecoveryStrategy(stateObjects);
    }

    @Test
    public void testSaveRollback() {
        // FIXME: tbd, the current RunloopRecoveryStrategy.rollback tries to
        // rollback to the state preceding the timestamp argument. Therefore
        // its impossible to rollback to the first state recorded.
        // This is actually an odd behavior but matches the needs of
        // EventRunloop best.
        strategy.save(-1L);
        strategy.save(0L);
        strategy.save(42L);
        
        strategy.rollback(42L);
        strategy.rollback(0L);

        verify(eventSource).save(-1L);
        verify(eventSource).save(0L);
        verify(eventSource).save(42L);

        verify(eventSource).rollback(-1L);
        verify(eventSource).rollback(0L);
    }

    @Test
    public void testSaveCommit() {
        strategy.save(0L);
        strategy.save(42L);
        
        strategy.commit(0L);
        strategy.commit(42L);

        verify(eventSource).save(0L);
        verify(eventSource).save(42L);
        verify(eventSource).commit(0L);
        verify(eventSource).commit(42L);
    }

    @Test(expected = StateHistoryException.class)
    public void testCommitWithInvalidKey() {
        strategy.commit(23L);
    }

    @Test(expected = StateHistoryException.class)
    public void testRollbackWithInvalidKey() {
        strategy.rollback(23L);
    }
}
