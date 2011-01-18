package model.items;


@SuppressWarnings("serial")
public class Point extends AbstractPoint {
    public Point(int x, int y) {
        super(x, y);
    }
    
    public Point(Point pill, int ownerId, Long time) {
        super(pill, ownerId, time);
    }
}
