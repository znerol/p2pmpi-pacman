package deism.tqgvt;

import deism.core.Event;
import deism.core.EventDispatcher;
import deism.core.EventExporter;
import deism.core.EventImporter;
import deism.core.Message;
import deism.core.MessageHandler;
import deism.core.MessageSender;
import deism.run.StateController;
import deism.run.SystemTimeProxy;
import deism.util.LongMap;

public class Client implements EventExporter, EventImporter, EventDispatcher,
        MessageHandler {
    private SystemTimeProxy systime;

    /**
     * The destination where gvt reports should be sent to
     */
    private final MessageSender master;

    /**
     * State controller of this simulation
     */
    private final StateController stateController;

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
    private LongMap<Long> recv;

    /**
     * 
     * @param process
     * @param tqlength
     */
    public Client(int process, long tqlength, StateController stateController,
            MessageSender master) {
        this.master = master;
        this.stateController = stateController;
        this.process = process;
        this.tqlength = tqlength;

        this.systime = new SystemTimeProxy();
        this.recv = new LongMap<Long>();

        this.lvt = 0;
        this.tq = -1;

        advanceTq(getCurrentTq());
    }

    @Override
    public Event unpack(Event event) {
        assert (event instanceof WrappedEvent);
        WrappedEvent wrappedEvent = (WrappedEvent) event;

        // register time quantum
        recv.get(wrappedEvent.getTq(), 0).add(1);

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
    public void handle(Message message) {
        assert (message instanceof GvtMessage);
        stateController.commit(((GvtMessage) message).getGvt());
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
