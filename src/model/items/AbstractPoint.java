package model.items;

import model.sprites.State;
import paclib.GamePlay;

public  abstract class AbstractPoint implements Item, State {
    private int owner;
    private int time = Integer.MAX_VALUE;
    private int id;
    private static int ID_OFFSET = Integer.MIN_VALUE;
    
    protected AbstractPoint() {
        id = ID_OFFSET++;
    }
    
    public int getPoints() {
        return GamePlay.POINTS_PER_POINT;
    }

    @Override
    public int getOwner(int time) {
        if (this.time <= time)
            return owner;
        return 0;
    }

    @Override
    public boolean isEaten(int time) {
        return this.time <= time;
    }

    @Override
    public void setOwner(int pac, int time) {
        this.owner = pac;
        this.time = time;
    }

    @Override
    public State getState(int time) {
        return this;
    }
    
    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public int getTimestamp() {
        // TODO Auto-generated method stub
        return 0;
    }
}
