package deism.p2pmpi;

import p2pmpi.mpi.IntraComm;
import deism.core.Startable;
import deism.ipc.async.BlockingReceiveOperation;
import deism.ipc.async.ReceiveThread;
import deism.ipc.base.Emitter;
import deism.ipc.base.Endpoint;
import deism.ipc.base.Message;

public class MpiUnicastListener implements Startable, Emitter<Message> {

    private final ReceiveThread<Message> receiver;

    public MpiUnicastListener(IntraComm comm, int mpisender, int mpitag) {
        BlockingReceiveOperation<Message> operation = new MpiReceiveOperation<Message>(
                comm, mpisender, mpitag);
        receiver = new ReceiveThread<Message>(operation);
    }

    @Override
    public void start(long simtime) {
        receiver.start();
    }

    @Override
    public void stop(long simtime) {
        receiver.terminate();
    }

    @Override
    public Endpoint<Message> getEndpoint(Class<Message> clazz) {
        return receiver.getEndpoint(clazz);
    }

    @Override
    public void setEndpoint(Endpoint<Message> endpoint) {
        receiver.setEndpoint(endpoint);
    }
}
