package deism.tqgvt;

import deism.core.Event;
import deism.core.EventExporter;
import deism.core.EventImporter;
import deism.process.DiscreteEventProcess;
import deism.run.SystemTimeProxy;
import deism.util.CounterMap;

public class Client implements EventExporter, EventImporter,
        DiscreteEventProcess {
    private SystemTimeProxy systime;

    /**
     * Process id of this process
     */
    private final int process;

    /**
     * Duration of a time quantum in milliseconds
     */
    private final long tqlength;

    /**
     * Current report event
     */
    private Event currentReport;

    /**
     * Current time quantum
     */
    private long tq;

    /**
     * Local virtual time. Timestamp of the last dispatched event.
     */
    private long lvt;

    /**
     * Minimum virtual time of the last message sent from the current time
     * quantum.
     */
    private long mvt;

    /**
     * Number of events sent in the current time quantum.
     */
    private int send;

    /**
     * Number of events received from other processes by time quantum.
     */
    private CounterMap<Long> recv;

    /**
     * 
     * @param process
     * @param tqlength
     */
    public Client(int process, long tqlength) {
        this.process = process;
        this.tqlength = tqlength;
        this.systime = new SystemTimeProxy();
        this.recv = new CounterMap<Long>();

        this.currentReport = null;
        this.lvt = 0;

        advanceTq(getCurrentTq());
    }

    @Override
    public Event unpack(Event event) {
        assert (event instanceof WrappedEvent);
        WrappedEvent wrappedEvent = (WrappedEvent) event;

        // register time quantum
        recv.increment(wrappedEvent.getTq(), 1);

        return wrappedEvent.getEvent();
    }

    @Override
    public Event pack(Event event) {
        updateReport();
        WrappedEvent wrappedEvent = new WrappedEvent(event, tq);
        return wrappedEvent;
    }

    @Override
    public void dispatchEvent(Event event) {
        lvt = Math.min(lvt, event.getSimtime());
    }

    @Override
    public Event peek(long simtime) {
        return currentReport;
    }

    @Override
    public void remove(Event event) {
        assert(event == currentReport);
        currentReport = null;
    }

    @Override
    public void offer(Event event) {
        // ignore
    }

    public void updateReport() {
        long newtq = getCurrentTq();

        if (newtq != tq) {
            // add new tq report to the queue and reset values
            currentReport = new ReportEvent(process, tq, lvt, mvt, send,
                    recv.valueMap());
            advanceTq(tq);
        }
    }

    public long getCurrentTq() {
        return systime.get() / tqlength;
    }

    private void advanceTq(long newTq) {
        assert(newTq > tq);
        send = 0;
        recv.clear();
        mvt = Long.MAX_VALUE;
    }
}
