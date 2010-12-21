package deism.tqgvt;

import deism.core.Event;
import deism.core.EventDispatcher;
import deism.core.EventExporter;
import deism.core.EventImporter;
import deism.core.MessageSender;
import deism.run.SystemTimeProxy;
import deism.util.CounterMap;

public class Client implements EventExporter, EventImporter, EventDispatcher {
    private SystemTimeProxy systime;

    /**
     * The destination where gvt reports should be sent to
     */
    private final MessageSender master;

    /**
     * Process id of this process
     */
    private final int process;

    /**
     * Duration of a time quantum in milliseconds
     */
    private final long tqlength;

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
    public Client(int process, long tqlength, MessageSender master) {
        this.master = master;
        this.process = process;
        this.tqlength = tqlength;

        this.systime = new SystemTimeProxy();
        this.recv = new CounterMap<Long>();

        this.lvt = 0;
        this.tq = -1;

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

    public void updateReport() {
        long newtq = getCurrentTq();

        if (newtq != tq) {
            // send new report message to gvt master
            master.send(new ReportMessage(process, tq, lvt, mvt, send, recv
                    .valueMap()));
            advanceTq(tq);
        }
    }

    public long getCurrentTq() {
        return systime.get() / tqlength;
    }

    private void advanceTq(long newTq) {
        assert (newTq > tq);
        send = 0;
        recv.clear();
        mvt = Long.MAX_VALUE;
        tq = newTq;
    }
}
