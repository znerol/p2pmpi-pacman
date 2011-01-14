package deism.p2pmpi;

import p2pmpi.mpi.IntraComm;

import deism.core.Event;
import deism.core.EventSink;
import deism.core.External;
import deism.core.Startable;
import deism.core.Stateful;
import deism.ipc.async.BlockingSendOperation;
import deism.ipc.async.SendThread;

/**
 * EventSink for outgoing Events to the specified receiver with a given tag
 * within an p2pmpi communicator.
 */
@Stateful
@External
public class MpiEventSink implements EventSink, Startable {

    private final SendThread<Event> sender;

    public MpiEventSink(IntraComm comm, int mpireceiver, int mpitag) {
        BlockingSendOperation<Event> operation = new MpiSendOperation<Event>(
                comm, mpireceiver, mpitag);
        sender = new SendThread<Event>(operation);
    }

    @Override
    public void offer(Event event) {
        sender.send(event);
    }

    @Override
    public void start(long simtime) {
        sender.start();
    }

    @Override
    public void stop(long simtime) {
        sender.terminate();
    }

    @Override
    public void join() {
        while(sender.getState() != Thread.State.TERMINATED) {
            try {
                sender.join();
            }
            catch (InterruptedException ex) {
                continue;
            }
        }
    }
}
