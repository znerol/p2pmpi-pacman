package deism.tqgvt;

import java.util.Arrays;
import java.util.Map.Entry;

import deism.core.Message;
import deism.core.MessageHandler;
import deism.core.MessageSender;
import deism.util.CounterMap;

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
    private CounterMap<Long> mvt;

    /**
     * Number of messages in transit by time quantum
     */
    private CounterMap<Long> transit;

    /**
     * @param processCount
     *            number of participating processes
     */
    public Master(int processCount, MessageSender clients) {
        this.clients = clients;
        this.gvt = 0;
        this.lvt = new long[processCount];
        this.mvt = new CounterMap<Long>();
        this.transit = new CounterMap<Long>();

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
        mvt.minimize(tq, reportEvent.getMvt());

        // increment number of sent messages for the senders tq in transit map
        transit.increment(tq, reportEvent.getSend());

        // decrement number of received messages from given tqs from transit
        // map.
        for (Entry<Long, Long> entry : reportEvent.getRecv().entrySet()) {
            transit.increment(entry.getKey(), -entry.getValue());
        }
    }

    public void updateGvt() {
        long newGvt = Long.MAX_VALUE;

        // find minimal lvt value of all processes
        for (long plvt : lvt) {
            newGvt = Math.min(newGvt, plvt);
        }

        for (Entry<Long, CounterMap<Long>.Counter> entry : mvt.entrySet()) {
            CounterMap<Long>.Counter t = transit.get(entry.getKey());
            long tqmvt = entry.getValue().get();
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
