package model;

/**
 * Directions for the sprites.
 */
public enum Direction {
    North, East, South, West, None;
    
    /**
     * Gives the inverse of a direction
     * @return current directions inverse
     */
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