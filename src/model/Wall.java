package model;

import java.util.ArrayList;
import java.util.List;

public class Wall extends Field {

    protected Wall(int x, int y, Board board) {
        super(x, y, board);
    }

    @Override
    public Street getVerticalStreet() {
        return null;
    }

    @Override
    public Street getHorizontalStreet() {
        return null;
    }

    @Override
    public List<Sprite> getSprites() {
        return new ArrayList<Sprite>();
    }
    
}
