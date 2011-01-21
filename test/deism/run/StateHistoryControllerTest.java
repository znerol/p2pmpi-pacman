package deism.run;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import deism.run.StateHistoryController;
import deism.stateful.StateHistory;
import deism.stateful.StateHistoryException;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class StateHistoryControllerTest {
    @Mock StateHistory<Long> stateObject;
    StateHistoryController stateController = new StateHistoryController();

    @Before
    public void setUp() {
        stateController.setStateObject(stateObject);
    }

    @Test
    public void testSaveRollback() {
        // FIXME: tbd, the current StateController.rollback tries to
        // rollback to the state preceding the timestamp argument. Therefore
        // its impossible to rollback to the first state recorded.
        // This is actually an odd behavior but matches the needs of
        // EventRunloop best.
        stateController.save(-1L);
        stateController.save(0L);
        stateController.save(42L);
        
        stateController.rollback(42L);
        stateController.rollback(0L);

        verify(stateObject).save(-1L);
        verify(stateObject).save(0L);
        verify(stateObject).save(42L);

        verify(stateObject).rollback(-1L);
        verify(stateObject).rollback(0L);
    }

    @Test
    public void testSaveCommit() {
        stateController.save(0L);
        stateController.save(42L);
        
        stateController.commit(0L);
        stateController.commit(42L);

        verify(stateObject).save(0L);
        verify(stateObject).save(42L);
        verify(stateObject).commit(0L);
        verify(stateObject).commit(42L);
    }

    @Test(expected = StateHistoryException.class)
    public void testRollbackWithInvalidKey() {
        stateController.rollback(23L);
    }
}
