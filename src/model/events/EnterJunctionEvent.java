package model.events;

import deism.util.Pair;

/**
 * This event informs the simulation about the prediction of a enter junction
 * from a sprite
 */
@SuppressWarnings("serial")
public class EnterJunctionEvent extends VisitableEvent {
    private final int x;
    private final int y;
    private final int sprite;

    public EnterJunctionEvent(int sprite, int x, int y, long simtime) {
        super(simtime);

        this.x = x;
        this.y = y;
        this.sprite = sprite;
    }

    /**
     * Sprite that is affected by this event
     * 
     * @return sprite di
     */
    public int getSprite() {
        return this.sprite;
    }

    /**
     * Event abscissa
     * 
     * @return event position
     */
    public int getX() {
        return this.x;
    }

    /**
     * Event ordinate
     * 
     * @return event position
     */
    public int getY() {
        return this.y;
    }

    /**
     * Event position
     * 
     * @return Event position
     */
    public Pair<Integer, Integer> getPosition() {
        return new Pair<Integer, Integer>(this.x, this.y);
    }

    @Override
    public String toString() {
        return (isAntimessage() ? "-" : "+") + "EnterJunctionEvent [simtime = "
                + getSimtime() + " sprite = " + sprite + " pos = "
                + getPosition() + "]";
    }

    @Override
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Object clone() {
        return new EnterJunctionEvent(this.sprite, this.x, this.y,
                this.getSimtime());
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
        if (!(obj instanceof EnterJunctionEvent))
            return false;
        EnterJunctionEvent other = (EnterJunctionEvent) obj;
        if (sprite != other.sprite)
            return false;
        if (x != other.x)
            return false;
        if (y != other.y)
            return false;
        return true;
    }
}
