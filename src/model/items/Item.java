package model.items;

import model.sprites.Sprite;
import model.sprites.State;

public interface Item extends Sprite, State {
    public boolean isEaten(Long time);
    public int getPoints();
    public int getOwnerId();
}
