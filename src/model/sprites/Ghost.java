package model.sprites;

import model.Model;


public class Ghost implements Sprite {
    private GhostState currentState;
    private final int id;
    private final Model model;
    
    public Ghost(GhostState initState, Model model) {
        this.currentState = initState;
        this.id = initState.getId();
        this.model = model;
    }
    
    @Override
    public String toString() {
        return "g";
    }

    @Override
    public int getId() {
        return this.id;
    }
}
