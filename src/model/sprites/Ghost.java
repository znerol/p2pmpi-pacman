package model.sprites;


public class Ghost implements Sprite {
    private GhostState state;
    private int id;
    
    public Ghost(GhostState initState, int id) {
        this.state = initState;
        this.id = id;
    }
    
    @Override
    public String toString() {
        return "g";
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
