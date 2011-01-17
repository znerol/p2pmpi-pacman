package model.items;


@SuppressWarnings("serial")
public class HappyPill extends AbstractPoint {
    public HappyPill(int x, int y) {
        super(x, y);
    }
    
    public HappyPill(HappyPill pill, int ownerId, Long time) {
        super(pill, ownerId, time);
    }
}
