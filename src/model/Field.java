package model;

import java.util.List;

public abstract class Field {
    protected int x;
    protected int y;
    protected Board board;
    
    protected Field(int x, int y, Board board) {
        
    }
    
    public abstract Street getVerticalStreet();
    
    public abstract Street getHorizontalStreet();
    
    public abstract List<Sprite> getSprites();
}
