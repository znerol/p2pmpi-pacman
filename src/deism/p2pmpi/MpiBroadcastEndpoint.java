package deism.p2pmpi;

import p2pmpi.mpi.IntraComm;
import deism.core.Startable;
import deism.ipc.async.BlockingSendOperation;
import deism.ipc.async.SendThread;
import deism.ipc.base.Message;
import deism.ipc.base.Endpoint;

public class MpiBroadcastEndpoint implements Endpoint<Message>, Startable {

    private final SendThread<Message> sender;

    public MpiBroadcastEndpoint(IntraComm mpicomm, int mpiroot) {
        assert(mpicomm.Rank() == mpiroot);
        BlockingSendOperation<Message> operation = new MpiBroadcastOperation<Message>(
                mpicomm, mpiroot);
        sender = new SendThread<Message>(operation);
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
    public void send(Message message) {
        sender.send(message);
    }
}
