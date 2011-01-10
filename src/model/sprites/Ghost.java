package model.sprites;


public class Ghost implements Sprite {
    private GhostState state;
    
    public Ghost(GhostState initState) {
        this.state = initState;
    }
    
    @Override
    public String toString() {
        return "g";
    }

    @Override
    public State getState(int time) {
        return this.state.getState(time);
    }
}
