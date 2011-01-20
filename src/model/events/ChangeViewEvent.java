package model.events;

import deism.util.Pair;

/**
 * This event informs the simulation that a change view is predicted by a
 * sprite. On every change of the view, every ghost has to check whether a
 * pacman is in his view or not.
 */
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

    /**
     * Event owner
     * 
     * @return owner sprite
     */
    public int getSprite() {
        return this.sprite;
    }

    /**
     * Getter event abscissa
     * 
     * @return event position
     */
    public int getX() {
        return this.x;
    }

    /**
     * Getter event ordinate
     * 
     * @return event position
     */
    public int getY() {
        return this.y;
    }

    /**
     * Getter event position
     * 
     * @return event position
     */
    public Pair<Integer, Integer> getPosition() {
        return new Pair<Integer, Integer>(this.x, this.y);
    }

    @Override
    public String toString() {
        return (isAntimessage() ? "-" : "+") + "ChangeViewEvent [simtime = "
                + getSimtime() + " sprite = " + sprite + " position = "
                + getPosition() + "]";
    }

    @Override
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Object clone() {
        return new ChangeViewEvent(this.sprite, this.x, this.y,
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
