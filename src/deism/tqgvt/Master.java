package deism.tqgvt;

import java.util.Arrays;
import java.util.Map.Entry;

import deism.core.Message;
import deism.core.MessageHandler;
import deism.core.MessageSender;
import deism.util.LongMap;
import deism.util.MutableLong;

public class Master implements MessageHandler {
    /**
     * Destination for gvt messages
     */
    private final MessageSender clients;

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
     * @param processCount
     *            number of participating processes
     */
    public Master(int processCount, MessageSender clients) {
        this.clients = clients;
        this.gvt = 0;
        this.lvt = new long[processCount];
        this.mvt = new LongMap<Long>();
        this.transit = new LongMap<Long>();

        Arrays.fill(this.lvt, Long.MAX_VALUE);
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

        if (newGvt != gvt) {
            assert (newGvt > gvt);
            clients.send(new GvtMessage(gvt));
        }
    }
}
