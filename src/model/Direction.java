package model;

public enum Direction {
    North, East, South, West, None;
    public Direction inverse() {
        switch (this) {
        case North:
            return Direction.South;
        case East:
            return Direction.West;
        case South:
            return Direction.North;
        case West:
            return Direction.East;
            default:
                return Direction.None;
        }
    }
}