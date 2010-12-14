package pingpong;

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
        return (isAntimessage() ? "-" : "+") + "[BallEvent timestamp="
                + getSimtime() + " from=" + sender + " to=" + receiver;
    }
    
    public int getSender() {
        return sender;
    }
    
    public int getReceiver() {
        return receiver;
    }
}
