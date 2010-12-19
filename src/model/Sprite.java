package model;

public class Sprite {

    public void getState(int t) {
        // TODO
    }

    private Direction direction = null;

    private Direction nextDirection = null;

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setNextDirection(Direction nextDirection) {
        this.nextDirection = nextDirection;
    }

    public Direction getNextDirection() {
        return nextDirection;
    }
}
