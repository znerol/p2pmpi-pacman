package model.events;


@SuppressWarnings("serial")
public class SpriteStoppedEvent extends VisitableEvent {

    private int sprite;
    
    public SpriteStoppedEvent(int sprite, long simtime) {
        super(simtime);
        
        this.sprite = sprite;
    }
    
    public int getSprite() {
        return this.sprite;
    }

    @Override
    public String toString() {
        return (isAntimessage() ? "-" : "+") + "SpriteStoppedEvent [simtime = "
                + getSimtime() + " sprite = " + sprite + "]";
    }

    @Override
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }
}
