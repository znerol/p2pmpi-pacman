package model;

import java.util.List;

public class StreetSegment extends Field {
    protected Street vertical;
    protected Street horizontal;

    protected StreetSegment(int x, int y, Board board) {
        super(x, y, board);
        vertical = new Street(this);
        horizontal = new Street(this);
    }

    @Override
    public Street getVerticalStreet() {
        return vertical;
    }

    @Override
    public Street getHorizontalStreet() {
        return horizontal;
    }

    @Override
    public List<Sprite> getSprites() {
        // TODO Auto-generated method stub
        return null;
    }
}
