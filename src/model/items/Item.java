package model.items;

import model.sprites.Sprite;

public interface Item extends Sprite {
    public int getOwner(int time);
    public void setOwner(int pac, int time);
    public boolean isEaten(int time);
    public int getPoints();
}
