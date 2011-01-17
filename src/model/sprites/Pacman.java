package model.sprites;

public class Pacman implements Sprite {
    private PacmanState state;
    private int id;
    
    public Pacman(PacmanState initState, int id) {
        this.state = initState;
        this.id = id;
    }

    @Override
    public String toString() {
        return "p";
    }

    @Override
    public State getState(int time) {
        return this.state.getState(time);
    }

    @Override
    public int getId() {
        return this.id;
    }
}
