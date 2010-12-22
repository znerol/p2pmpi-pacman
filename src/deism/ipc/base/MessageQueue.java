package deism.ipc.base;

public interface MessageQueue extends MessageHandler {
    public Message poll();
}
