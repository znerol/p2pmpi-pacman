package model.items;

import model.Pair;
import model.sprites.Sprite;

public interface Item extends Sprite {
    public boolean isEaten(Long time);
    public int getPoints();
    public int getOwnerId();
    public Pair<Integer, Integer> getPosition();
}
