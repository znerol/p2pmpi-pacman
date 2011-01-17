package model.items;


public class HappyPill extends AbstractPoint {
    public HappyPill() {
        super();
    }
    
    public HappyPill(HappyPill pill, int ownerId, Long time) {
        super(pill, ownerId, time);
    }
}
