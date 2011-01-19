package model.events;

import model.Pair;


@SuppressWarnings("serial")
public class CollisionEvent extends VisitableEvent {
    private final int sprite1;
    private final int sprite2;
    
    public CollisionEvent(int sprite1, int sprite2, long simtime) {
        super(simtime);
        
        this.sprite1 = sprite1;
        this.sprite2 = sprite2;
    }
    
    public int getSprite1() {
        return this.sprite1;
    }
    
    public int getSprite2() {
        return this.sprite2;
    }
    
    public Pair<Integer, Integer> getSprites() {
        return new Pair<Integer, Integer>(this.sprite1, this.sprite2);
    }

    @Override
    public String toString() {
        return (isAntimessage() ? "-" : "+") + "SpriteStoppedEvent [simtime = "
                + getSimtime() + " sprite1 = " + sprite1 + " sprite2 = " + sprite2 + "]";
    }

    @Override
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public Object clone() {
        return new CollisionEvent(this.sprite1, this.sprite2, this.getSimtime());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + sprite1;
        result = prime * result + sprite2;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (!(obj instanceof CollisionEvent))
            return false;
        CollisionEvent other = (CollisionEvent) obj;
        if (sprite1 != other.sprite1)
            return false;
        if (sprite2 != other.sprite2)
            return false;
        return true;
    }
}
