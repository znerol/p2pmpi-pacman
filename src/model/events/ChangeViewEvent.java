package model.events;

import model.Pair;

@SuppressWarnings("serial")
public class ChangeViewEvent extends VisitableEvent {
    private final int sprite;
    private final int x;
    private final int y;
    
    public ChangeViewEvent(int sprite, int x, int y, long simtime) {
        super(simtime);
        
        this.sprite = sprite;
        this.x = x;
        this.y = y;
    }
    
    public int getSprite() {
        return this.sprite;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public Pair<Integer, Integer> getPosition() {
        return new Pair<Integer, Integer>(this.x, this.y);
    }

    @Override
    public String toString() {
        return (isAntimessage() ? "-" : "+") + "ChangeViewEvent [simtime = "
                + getSimtime() + " sprite = " + sprite + " position = " + getPosition() + "]";
    }

    @Override
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public Object clone() {
        return new ChangeViewEvent(this.sprite, this.x, this.y, this.getSimtime());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + sprite;
        result = prime * result + x;
        result = prime * result + y;
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
        if (x != other.x)
            return false;
        if (y != other.y)
            return false;
        return true;
    }
}
