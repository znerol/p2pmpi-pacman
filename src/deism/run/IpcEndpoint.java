package deism.run;

import java.util.ArrayDeque;
import java.util.Queue;

import deism.ipc.base.Endpoint;
import deism.ipc.base.Message;

public class IpcEndpoint implements Endpoint<Message> {

    private final ExecutionGovernor governor;
    private final Queue<Message> queue = new ArrayDeque<Message>();

    public IpcEndpoint(ExecutionGovernor governor) {
        this.governor = governor;
    }

    public synchronized Message poll() {
        return queue.poll();
    }

    @Override
    public void send(Message item) {
        synchronized (this) {
            queue.offer(item);
        }
        governor.resume();
    }
}
