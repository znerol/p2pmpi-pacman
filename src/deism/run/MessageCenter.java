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

/**
 * Part of the runloop where ipc messages are exchanged.
 */
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

    /**
     * Loop thru all available messages and dispatch them.
     */
    public synchronized void process() {
        Message message;
        while ((message = queue.poll()) != null) {
            dispatch(message);
        }
    }

    /**
     * Deliver the given message to every handler and endpoint known to this
     * MessageCenter
     * 
     * @param message
     *            Message to dispatch
     */
    private void dispatch(Message message) {
        for (Handler<Message> handler : handlers) {
            handler.handle(message);
        }
        for (Endpoint<Message> endpoint : endpoints) {
            endpoint.send(message);
        }
    }

    /**
     * Register the given handler in this message center.
     * 
     * @param handler
     *            message handler
     */
    public void addHandler(Handler<Message> handler) {
        handlers.add(handler);
    }

    /**
     * Register the given handler in this message center for all messages
     * satisfying the given condition.
     * 
     * @param handler
     *            message handler
     * @param condition
     *            filter condition for messages
     */
    public void addHandler(Handler<Message> handler,
            Condition<Message> condition) {
        handlers.add(new FilteredHandler<Message>(handler, condition));
    }

    /**
     * Register the given endpoint in this message center.
     * 
     * @param endpoint
     *            message endpoint
     */
    public void addEndpoint(Endpoint<Message> Endpoint) {
        endpoints.add(Endpoint);
    }

    /**
     * Register the given endpoint in this message center for all messages
     * satisfying the given condition.
     * 
     * @param endpoint
     *            message endpoint
     * @param condition
     *            filter condition for messages
     */
    public void addEndpoint(Endpoint<Message> endpoint,
            Condition<Message> condition) {
        endpoints.add(new FilteredEndpoint<Message>(endpoint, condition));
    }

    /**
     * Set the endpoint of the given emitter to this message center.
     * 
     * @param emitter
     *            message emitter
     */
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
