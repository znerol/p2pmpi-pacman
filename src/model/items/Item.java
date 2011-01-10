package model.items;

import model.sprites.Pacman;

public interface Item {
    public Pacman getOwner(int time);
    public void setOwner(Pacman pac, int time);
    public boolean isEaten(int time);
    public int getPoints();
}
