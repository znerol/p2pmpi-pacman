package deism.run;

import org.junit.Test;

import deism.run.FailFastRunloopRecoveryStrategy;
import deism.stateful.StateHistoryException;

public class FailFastRunloopRecoveryStrategyTest {
    FailFastRunloopRecoveryStrategy strategy =
        new FailFastRunloopRecoveryStrategy();

    @Test
    public void testSaveCommit() {
        // no-op
        strategy.save(42L);
        strategy.commit(42L);
    }

    @Test(expected=StateHistoryException.class)
    public void testRollback() {
        strategy.save(42L);
        strategy.rollback(42L);
    }
}
