package pacifism;

import deism.core.Event;
import deism.core.EventDispatcher;
import deism.stateful.StateHistory;
import deism.stateful.StateHistoryException;
import deism.tqgvt.Client;

public class GvtStats implements StateHistory<Long>, EventDispatcher {
    private int saves;
    private long lastSave;
    private int comits;
    private int rollbacks;
    private long lvt;
    private long gvt;

    private final Client gvtClient;

    public GvtStats(Client gvtClient) {
        this.gvtClient = gvtClient;
    }

    @Override
    public void dispatchEvent(Event e) {
        lvt = e.getSimtime();
    }

    @Override
    public void save(Long key) throws StateHistoryException {
        lastSave = key;
        saves++;
    }

    @Override
    public void commit(Long key) throws StateHistoryException {
        gvt = key;
        comits++;
    }

    @Override
    public void rollback(Long key) throws StateHistoryException {
        lvt = Math.min(key, lvt);
        rollbacks++;
    }

    @Override
    public String toString() {
        long tq = gvtClient.getCurrentTq();
        return "LVT: " + lvt + " GVT: " + gvt + " TQ: " + tq + " Saves: "
                + saves + " lastSave: " + lastSave + " Comits: " + comits
                + " Rollbacks: " + rollbacks;
    }
}
