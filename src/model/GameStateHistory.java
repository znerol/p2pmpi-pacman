package model;

import java.util.List;

import wq2.Counter;
import deism.stateful.AbstractStateHistory;

public class GameStateHistory extends AbstractStateHistory<Long, GameState> {

    @Override
    public void revertHistory(List<GameState> tail) {
        // TODO Auto-generated method stub
        
    }

}
