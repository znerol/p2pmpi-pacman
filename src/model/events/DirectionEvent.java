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
}
