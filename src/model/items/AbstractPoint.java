package model.items;

import model.sprites.Pacman;
import paclib.GamePlay;

public abstract class AbstractPoint implements Item {
    private Pacman owner;
    private int time = Integer.MAX_VALUE;
    
    public int getPoints() {
        return GamePlay.POINTS_PER_POINT;
    }

    @Override
    public Pacman getOwner(int time) {
        if (this.time <= time)
            return owner;
        return null;
    }

    @Override
    public boolean isEaten(int time) {
        return this.time <= time;
    }

    @Override
    public void setOwner(Pacman pac, int time) {
        this.owner = pac;
        this.time = time;
    }
}
