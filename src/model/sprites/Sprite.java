package model.sprites;

import java.io.Serializable;

import model.Direction;
import model.Triple;
import model.events.EventVisitor;


public interface Sprite extends Cloneable, Serializable, EventVisitor {    
    public int getId();
    public Object clone();
    public Long getTimestamp();
    public Triple<Direction, Integer, Integer> nextPosition(Long simTime);
}