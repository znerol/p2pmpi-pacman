package deism.run;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import deism.ipc.base.Endpoint;
import deism.ipc.base.Handler;
import deism.ipc.base.Message;

public class MessageCenter implements Endpoint<Message> {

    private final ExecutionGovernor governor;
    private final Queue<Message> queue = new ArrayDeque<Message>();
    private final List<Handler<Message>> handlers =
            new ArrayList<Handler<Message>>();

    public MessageCenter(ExecutionGovernor governor) {
        this.governor = governor;
    }

    public synchronized Message poll() {
        return queue.poll();
    }

    public void process() {
        Message message;
        while ((message = queue.poll()) != null) {
            dispatch(message);
        }
    }

    public void dispatch(Message message) {
        for (Handler<Message> handler : handlers) {
            handler.handle(message);
        }
    }

    public void addHandler(Handler<Message> handler) {
        handlers.add(handler);
    }

    @Override
    public void send(Message item) {
        synchronized (this) {
            queue.offer(item);
        }
        governor.resume();
    }
}
