package deism.p2pmpi;

import p2pmpi.mpi.IntraComm;
import deism.core.Startable;
import deism.ipc.async.ReceiveThread;
import deism.ipc.async.SendThread;
import deism.ipc.base.Emitter;
import deism.ipc.base.Message;
import deism.ipc.base.Endpoint;

public class MpiBroadcast implements Endpoint<Message>, Emitter<Message>,
        Startable {

    private final SendThread<Message> sender;
    private final ReceiveThread<Message> receiver;

    public MpiBroadcast(IntraComm mpicomm, int mpiroot) {

        MpiBroadcastOperation<Message> operation =
                new MpiBroadcastOperation<Message>(mpicomm, mpiroot);

        if (mpicomm.Rank() == mpiroot) {
            sender = new SendThread<Message>(operation);
            receiver = null;
        }
        else {
            sender = null;
            receiver = new ReceiveThread<Message>(operation);
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
            receiver.terminate();
        }
    }

    @Override
    public void join() {
        if (sender != null) {
            while(sender.getState() != Thread.State.TERMINATED) {
                try {
                    sender.join();
                }
                catch (InterruptedException ex) {
                    continue;
                }
            }
        }
        else {
            while(receiver.getState() != Thread.State.TERMINATED) {
                try {
                    receiver.join();
                }
                catch (InterruptedException ex) {
                    continue;
                }
            }
        }
    }

    @Override
    public void send(Message message) {
        assert (sender != null);
        sender.send(message);
    }

    @Override
    public Endpoint<Message> getEndpoint(Class<Message> clazz) {
        assert (receiver != null);
        return receiver.getEndpoint(clazz);
    }

    @Override
    public void setEndpoint(Endpoint<Message> endpoint) {
        assert (receiver != null);
        receiver.setEndpoint(endpoint);
    }
}
