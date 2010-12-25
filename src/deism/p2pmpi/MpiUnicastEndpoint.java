package deism.p2pmpi;

import p2pmpi.mpi.IntraComm;
import deism.core.Startable;
import deism.ipc.async.BlockingSendOperation;
import deism.ipc.async.SendThread;
import deism.ipc.base.Message;
import deism.ipc.base.Endpoint;

public class MpiUnicastEndpoint implements Endpoint<Message>, Startable {

    private final SendThread<Message> sender;

    public MpiUnicastEndpoint(IntraComm mpicomm, int mpireceiver, int mpitag) {
        BlockingSendOperation<Message> operation = new MpiSendOperation<Message>(
                mpicomm, mpireceiver, mpitag);
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
