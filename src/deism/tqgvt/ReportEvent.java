package deism.tqgvt;

import java.util.Map;

import deism.core.Event;

public class ReportEvent extends Event {
    private static final long serialVersionUID = 4825754443295424225L;

    /**
     * Process id of the reporting process
     */
    private final int process;

    /**
     * Time quantum of the reporting process
     */
    private final long tq;

    /**
     * Local virtual time of the reporting process
     */
    private final long lvt;

    /**
     * Timestamp in simulation time units of the earliest event sent from the
     * reporting process during this time quantum.
     */
    private final long mvt;

    /**
     * Number of events sent from the reporting processor during the time
     * quantum.
     */
    private final long send;

    /**
     * Number of events received during the time quantum, indexed by the tq of
     * the sending process.
     */
    private final Map<Long, Long> recv;

    public ReportEvent(int process, long tq, long lvt, long mvt, long send,
            Map<Long, Long> recv) {
        super(0);
        this.process = process;
        this.tq = tq;
        this.lvt = lvt;
        this.mvt = mvt;
        this.send = send;
        this.recv = recv;
    }

    public int getProcess() {
        return process;
    }

    public long getTq() {
        return tq;
    }

    public long getLvt() {
        return lvt;
    }

    public long getMvt() {
        return mvt;
    }

    public long getSend() {
        return send;
    }

    public Map<Long, Long> getRecv() {
        return recv;
    }
}
