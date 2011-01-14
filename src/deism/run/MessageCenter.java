package deism.run;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import deism.ipc.base.Condition;
import deism.ipc.base.Emitter;
import deism.ipc.base.Endpoint;
import deism.ipc.base.FilteredEndpoint;
import deism.ipc.base.FilteredHandler;
import deism.ipc.base.Handler;
import deism.ipc.base.Message;

public class MessageCenter implements Endpoint<Message> {

    private final ExecutionGovernor governor;
    private final Queue<Message> queue = new ArrayDeque<Message>();
    private final List<Handler<Message>> handlers =
        new ArrayList<Handler<Message>>();
    private final List<Endpoint<Message>> endpoints =
        new ArrayList<Endpoint<Message>>();

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
        for (Endpoint<Message> endpoint : endpoints) {
            endpoint.send(message);
        }
    }

    public void addHandler(Handler<Message> handler) {
        handlers.add(handler);
    }

    public void addHandler(Handler<Message> handler,
            Condition<Message> condition) {
        handlers.add(new FilteredHandler<Message>(handler, condition));
    }

    public void addEndpoint(Endpoint<Message> Endpoint) {
        endpoints.add(Endpoint);
    }

    public void addEndpoint(Endpoint<Message> endpoint,
            Condition<Message> condition) {
        endpoints.add(new FilteredEndpoint<Message>(endpoint, condition));
    }

    public void addEmitter(Emitter<Message> emitter) {
        emitter.setEndpoint(this);
    }

    @Override
    public void send(Message item) {
        synchronized (this) {
            queue.offer(item);
        }
        governor.resume();
    }
}
