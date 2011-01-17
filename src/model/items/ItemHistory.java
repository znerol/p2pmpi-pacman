package model.items;

import java.util.List;

import deism.stateful.AbstractStateHistory;

public class ItemHistory extends AbstractStateHistory<Long, Item> {

    @Override
    public void revertHistory(List<Item> tail) {
        // TODO Auto-generated method stub
        
    }

}
