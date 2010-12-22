package deism.run;

import java.util.ArrayDeque;
import java.util.Queue;

import deism.ipc.base.Message;
import deism.ipc.base.MessageQueue;

public class DefaultRunloopMessageQueue implements MessageQueue {

    private final ExecutionGovernor governor;
    private final Queue<Message> queue = new ArrayDeque<Message>();

    public DefaultRunloopMessageQueue(ExecutionGovernor governor) {
        this.governor = governor;
    }

    @Override
    public void handle(Message item) {
        synchronized (this) {
            queue.offer(item);
        }
        governor.resume();
    }

    @Override
    public synchronized Message poll() {
        return queue.poll();
    }
}
