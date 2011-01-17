package model.sprites;



public interface State extends Cloneable {
    public State getState(int time);
    public int getTimestamp();
}
