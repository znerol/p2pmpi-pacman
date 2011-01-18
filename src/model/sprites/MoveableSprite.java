package model.sprites;

import model.Direction;

public interface MoveableSprite extends Sprite {
    public Direction getCurrentDirection();
    public Direction getNextDirection();

}
