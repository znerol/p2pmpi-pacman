package deism;

public interface EventRunloopRecoveryStrategy extends StateHistory <Long> {
    public boolean shouldRollback(Event e);
    public boolean shouldSave(Event e);
}
