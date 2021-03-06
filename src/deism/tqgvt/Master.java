package deism.tqgvt;

import java.util.Arrays;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import deism.ipc.base.Emitter;
import deism.ipc.base.Handler;
import deism.ipc.base.Message;
import deism.ipc.base.Endpoint;
import deism.util.LongMap;
import deism.util.MutableLong;

/**
 * Time quantum GVT master implementation
 * 
 * @see "<a href='http://www.cs.rpi.edu/~szymansk/papers/scpe.07.pdf'>TQ-GVT</a>
 *      TIME QUANTUM GVT: A SCALABLE COMPUTATION OF THE GLOBAL VIRTUAL TIME IN
 *      PARALLEL DISCRETE EVENT SIMULATIONS by GILBERT G. CHEN AND BOLESLAW K.
 *      SZYMANSKI, Scalable Computing: Practice and Experience, vol. 8, no. 4,
 *      2008, pp. 423-435"
 */
public class Master implements Handler<Message>, Emitter<Message> {
    /**
     * Destination for gvt messages
     */
    private Endpoint<Message> clients;

    /**
     * Global virtual time
     */
    private long gvt;

    /**
     * Local virtual time of each participating process
     */
    private long lvt[];

    /**
     * Minimal virtual time of outgoing events by time quantum
     */
    private LongMap<Long> mvt;

    /**
     * Number of messages in transit by time quantum
     */
    private LongMap<Long> transit;

    /**
     * log4j
     */
    private static final Logger logger = Logger.getLogger(Master.class);

    /**
     * @param processCount
     *            number of participating processes
     */
    public Master(int processCount) {
        this.gvt = 0;
        this.lvt = new long[processCount];
        this.mvt = new LongMap<Long>();
        this.transit = new LongMap<Long>();
    }

    @Override
    public void handle(Message message) {
        assert (message instanceof ReportMessage);
        processReport((ReportMessage) message);
        updateGvt();
    }

    public void processReport(ReportMessage reportEvent) {
        // store local virtual time of this process
        lvt[reportEvent.getProcess()] = reportEvent.getLvt();

        // store
        long tq = reportEvent.getTq();
        mvt.get(tq, Long.MAX_VALUE).min(reportEvent.getMvt());

        // increment number of sent messages for the senders tq in transit map
        transit.get(tq, 0).add(reportEvent.getSend());

        // decrement number of received messages from given tqs from transit
        // map.
        for (Entry<Long, Long> entry : reportEvent.getRecv().entrySet()) {
            transit.get(entry.getKey(), 0).add(-entry.getValue());
        }

        logger.debug("Finished processing gvt report");
        logger.debug("  lvt=" + Arrays.toString(lvt));
        logger.debug("  mvt=" + mvt.valueMap());
        logger.debug("  tns=" + transit.valueMap());
    }

    public void updateGvt() {
        long newGvt = Long.MAX_VALUE;

        // find minimal lvt value of all processes
        for (long plvt : lvt) {
            newGvt = Math.min(newGvt, plvt);
        }

        for (Entry<Long, MutableLong> entry : mvt.entrySet()) {
            long tqmvt = entry.getValue().get();
            MutableLong t = transit.get(entry.getKey());
            if (t != null && t.get() != 0 && tqmvt != 0) {
                newGvt = Math.min(newGvt, tqmvt);
            }
        }

        logger.debug("Result of gvt calculation newGvt=" + newGvt + " current gvt=" + gvt);

        if (newGvt != gvt) {
            assert (newGvt > gvt);
            gvt = newGvt;
            logger.info("New GVT: " + gvt);
            clients.send(new GvtMessage(gvt));
        }
    }

    @Override
    public Endpoint<Message> getEndpoint(Class<Message> clazz) {
        return clients;
    }

    @Override
    public void setEndpoint(Endpoint<Message> endpoint) {
        this.clients = endpoint;
    }
}
