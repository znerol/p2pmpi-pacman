package deism;

import org.junit.Test;
import static org.junit.Assert.*;

public class ImmediateExecutionGovernorTest {
    ImmediateExecutionGovernor governor = new ImmediateExecutionGovernor();

    @Test
    public void testStartStop() {
        // no-op
        governor.start(42L);
        governor.stop();
    }

    @Test
    public void testSuspend() {
        long result = governor.suspend();
        assertEquals(Long.MAX_VALUE, result);
    }

    @Test
    public void testSuspendUntil() {
        long result = governor.suspendUntil(42L);
        assertEquals(42L, result);
    }

    @Test
    public void testResume() {
        // no-op
        governor.resume();
        governor.resume(42L);
    }
}
