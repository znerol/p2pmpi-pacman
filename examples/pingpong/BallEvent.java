package pingpong;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import deism.Event;

public class BallEvent extends Event {
    private int sender;
    private int receiver;
    
    /**
     * 
     */
    private static final long serialVersionUID = -4440991512966102852L;

    public BallEvent(long simtime, int sender, int receiver) {
        super(simtime);
        this.sender = sender;
        this.receiver = receiver;
    }

    public String toString() {
        return "[BallEvent timestamp=" + getSimtime() + " from=" + sender +
            " to=" + receiver;
    }
    
    public int getSender() {
        return sender;
    }
    
    public int getReceiver() {
        return receiver;
    }

    protected void writeObject(ObjectOutputStream out) throws IOException {
        super.writeObject(out);
        out.writeInt(sender);
        out.writeInt(receiver);
    }

    protected void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        super.readObject(in);
        sender = in.readInt();
        receiver = in.readInt();
    }
}
