package model.events;

import model.Direction;

/**
 * Change direction command for controllable sprites.
 */
public class DirectionEvent extends VisitableEvent {
    private static final long serialVersionUID = 8246933526612570626L;
    private final int sprite;
    private final Direction direction;

    public DirectionEvent(long currentSimtime, int sprite, Direction direction) {
        super(currentSimtime);
        this.sprite = sprite;
        this.direction = direction;
    }

    @Override
    public String toString() {
        return (isAntimessage() ? "-" : "+") + "DirectionEvent [simtime = "
                + getSimtime() + " sprite = " + sprite + " direction = "
                + direction + "]";
    }

    public int getSprite() {
        return sprite;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public Object clone() {
        return new DirectionEvent(this.getSimtime(), this.sprite, this.direction);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
                + ((direction == null) ? 0 : direction.hashCode());
        result = prime * result + sprite;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (!(obj instanceof DirectionEvent))
            return false;
        DirectionEvent other = (DirectionEvent) obj;
        if (direction != other.direction)
            return false;
        if (sprite != other.sprite)
            return false;
        return true;
    }
}
