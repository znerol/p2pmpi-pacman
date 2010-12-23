package deism.tqgvt;

import deism.core.Event;
import deism.ipc.base.Emitter;
import deism.ipc.base.EventExporter;
import deism.ipc.base.EventImporter;
import deism.ipc.base.Handler;
import deism.ipc.base.Message;
import deism.ipc.base.Endpoint;
import deism.run.LvtListener;
import deism.run.StateController;
import deism.run.SystemTimeProxy;
import deism.util.LongMap;

/**
 * Time quantum GVT client implementation
 * 
 * @see <a href="http://www.cs.rpi.edu/~szymansk/papers/scpe.07.pdf">TQ-GVT</a>
 *      TIME QUANTUM GVT: A SCALABLE COMPUTATION OF THE GLOBAL VIRTUAL TIME IN
 *      PARALLEL DISCRETE EVENT SIMULATIONS by GILBERT G. CHEN AND BOLESLAW K.
 *      SZYMANSKI, Scalable Computing: Practice and Experience, vol. 8, no. 4,
 *      2008, pp. 423-435
 */
public class Client implements EventExporter, EventImporter, LvtListener,
        Handler<Message>, Emitter<Message> {
    private SystemTimeProxy systime;

    /**
     * The destination where gvt reports should be sent to
     */
    private Endpoint<Message> master;

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
    public Client(int process, long tqlength, StateController stateController) {
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

        // increment receive count for the time quantum this message came from
        recv.get(wrappedEvent.getTq(), 0).add(1);

        // unwrap event
        return wrappedEvent.getEvent();
    }

    @Override
    public Event pack(Event event) {
        // send a new report to the master if necessary and update tq
        updateReport();

        // update the minimum virtual time we're sending a message for
        mvt = Math.min(mvt, event.getSimtime());

        // increment send count for the current time quantum
        send++;

        // wrap up event into a tq-gvt event and associate our current tq with
        // it.
        WrappedEvent wrappedEvent = new WrappedEvent(event, tq);
        return wrappedEvent;
    }

    /**
     * Update local virtual time
     */
    @Override
    public void update(long lvt) {
        this.lvt = lvt;
    }

    /**
     * Handle a gvt message from the master. Commit the states up to but not
     * including gvt.
     */
    @Override
    public void handle(Message message) {
        assert (message instanceof GvtMessage);
        stateController.commit(((GvtMessage) message).getGvt());
    }

    /**
     * Calculate current time quantum. If it changed from the previous value,
     * create a new TQ-GVT report message and send it to the tq master.
     */
    public void updateReport() {
        long newtq = getCurrentTq();

        if (newtq != tq) {
            // send new report message to gvt master
            master.send(new ReportMessage(process, tq, lvt, mvt, send, recv
                    .valueMap()));
            advanceTq(newtq);
        }
    }

    /**
     * Calculate current time quantum.
     * 
     * @return current time quantum
     */
    public long getCurrentTq() {
        return systime.get() / tqlength;
    }

    /**
     * Reset gvt report variables and advance to given time quantum.
     * 
     * @param newTq
     *            new time quantum
     */
    private void advanceTq(long newTq) {
        assert (newTq > tq);
        send = 0;
        recv.clear();
        mvt = Long.MAX_VALUE;
        tq = newTq;
    }

    @Override
    public Endpoint<Message> getEndpoint(Class<Message> clazz) {
        return master;
    }

    @Override
    public void setEndpoint(Endpoint<Message> endpoint) {
        this.master = endpoint;
    }
}
