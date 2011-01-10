package model.sprites;

public class Pacman implements Sprite {
    private PacmanState state;
    private int rank;
    
    public Pacman(PacmanState initState, int rank) {
        this.state = initState;
        this.rank = rank;
    }
    
    public int getRank() {
        return this.rank;
    }

    @Override
    public String toString() {
        return "p";
    }

    @Override
    public State getState(int time) {
        return this.state.getState(time);
    }
}
