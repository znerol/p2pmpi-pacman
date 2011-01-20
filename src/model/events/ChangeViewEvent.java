package model.events;

@SuppressWarnings("serial")
public class ChangeViewEvent extends VisitableEvent {
    private int sprite;
    
    public ChangeViewEvent(int sprite, long simtime) {
        super(simtime);
        
        this.sprite = sprite;
    }
    
    public int getSprite() {
        return this.sprite;
    }

    @Override
    public String toString() {
        return (isAntimessage() ? "-" : "+") + "ChangeViewEvent [simtime = "
                + getSimtime() + " sprite = " + sprite + "]";
    }

    @Override
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public Object clone() {
        return new ChangeViewEvent(this.sprite, this.getSimtime());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + sprite;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (!(obj instanceof ChangeViewEvent))
            return false;
        ChangeViewEvent other = (ChangeViewEvent) obj;
        if (sprite != other.sprite)
            return false;
        return true;
    }
}
