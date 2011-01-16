package deism.run;

/**
 * Helper class in order to avoid call to non-mockable static functions from
 * {@link RealtimeExecutionGovernor}.
 */
public class SystemTimeProxy {
    public long get() {
        return System.currentTimeMillis();
    }
}
