package deism.p2pmpi;

import p2pmpi.mpi.IntraComm;
import deism.core.Startable;
import deism.ipc.async.BlockingReceiveOperation;
import deism.ipc.async.ReceiveThread;
import deism.ipc.base.Message;
import deism.ipc.base.MessageHandler;

public class MpiMessageReceiver implements Startable {

    private final ReceiveThread<Message> receiver;

    public MpiMessageReceiver(IntraComm comm, int mpisender, int mpitag,
            MessageHandler handler) {
        BlockingReceiveOperation<Message> operation = new MpiReceiveOperation<Message>(
                comm, mpisender, mpitag);
        receiver = new ReceiveThread<Message>(operation, handler);
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
