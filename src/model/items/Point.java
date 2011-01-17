package model.items;


public class Point extends AbstractPoint {
    public Point() {
        super();
    }
    
    public Point(Point pill, int ownerId, Long time) {
        super(pill, ownerId, time);
    }
}
