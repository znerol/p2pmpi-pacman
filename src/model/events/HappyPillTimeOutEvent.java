package model.events;

@SuppressWarnings("serial")
public class HappyPillTimeOutEvent extends VisitableEvent {
    
    public HappyPillTimeOutEvent(long simtime) {
        super(simtime);
    }

    @Override
    public String toString() {
        return (isAntimessage() ? "-" : "+") + "HappyPillTimeEndedEvent [simtime = "
                + getSimtime() + "]";
    }

    @Override
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }
}
