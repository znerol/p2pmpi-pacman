package deism.p2pmpi;

import p2pmpi.mpi.IntraComm;
import deism.core.Startable;
import deism.ipc.async.ReceiveThread;
import deism.ipc.async.SendThread;
import deism.ipc.base.Message;
import deism.ipc.base.Endpoint;

public class MpiBroadcast implements Endpoint<Message>, Startable {

    private final SendThread<Message> sender;
    private final ReceiveThread<Message> receiver;

    public MpiBroadcast(IntraComm mpicomm, int mpiroot,
            Endpoint<Message> endpoint) {

        MpiBroadcastOperation<Message> operation = new MpiBroadcastOperation<Message>(
                mpicomm, mpiroot);

        if (mpicomm.Rank() == mpiroot) {
            sender = new SendThread<Message>(operation);
            receiver = null;
        }
        else {
            sender = null;
            receiver = new ReceiveThread<Message>(operation, endpoint);
        }
    }

    @Override
    public void start(long simtime) {
        if (sender != null) {
            sender.start();
        }
        else {
            receiver.start();
        }
    }

    @Override
    public void stop(long simtime) {
        if (sender != null) {
            sender.terminate();
        }
        else {
            receiver.start();
        }
    }

    @Override
    public void send(Message message) {
        assert (sender != null);
        sender.send(message);
    }
}
