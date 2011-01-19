package model.sprites;

import java.io.Serializable;

import model.Direction;
import model.Pair;
import model.Triple;
import model.events.EventVisitor;


public interface SpriteState extends Cloneable, Serializable, EventVisitor {    
    public int getId();
    public Object clone();
    public Long getTimestamp();
    public Pair<Integer, Integer> getOrigin();
    public Triple<Direction, Integer, Integer> nextPosition(Long simTime);
}