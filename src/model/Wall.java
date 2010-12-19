package model;

public class Wall extends Field {

    protected Wall(int x, int y, Board board) {
        super(x, y, board);
    }
    
    @Override
    public Sprite getSprite() {
        return null;
    }    
}
