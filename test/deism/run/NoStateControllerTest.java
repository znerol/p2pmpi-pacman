package deism.run;

import org.junit.Test;

import deism.run.NoStateController;
import deism.stateful.StateHistoryException;

public class NoStateControllerTest {
    NoStateController stateController =
        new NoStateController();

    @Test
    public void testSaveCommit() {
        // no-op
        stateController.save(42L);
        stateController.commit(42L);
    }

    @Test(expected=StateHistoryException.class)
    public void testRollback() {
        stateController.save(42L);
        stateController.rollback(42L);
    }
}
