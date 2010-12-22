package deism.p2pmpi;

import p2pmpi.mpi.IntraComm;
import deism.core.Startable;
import deism.ipc.async.BlockingReceiveOperation;
import deism.ipc.async.ReceiveThread;
import deism.ipc.base.Endpoint;
import deism.ipc.base.Message;

public class MpiBroadcastListener implements Startable {

    private final ReceiveThread<Message> receiver;

    public MpiBroadcastListener(IntraComm comm, int mpiroot,
            Endpoint<Message> endpoint) {
        assert(comm.Rank() != mpiroot);
        BlockingReceiveOperation<Message> operation = new MpiBroadcastOperation<Message>(
                comm, mpiroot);
        receiver = new ReceiveThread<Message>(operation, endpoint);
    }

    @Override
    public void start(long simtime) {
        receiver.start();
    }

    @Override
    public void stop(long simtime) {
        receiver.terminate();
    }
}
