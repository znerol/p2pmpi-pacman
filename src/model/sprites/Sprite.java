package model.sprites;


public interface Sprite {
    public State getState(int time);
    
    public int getId();
}
