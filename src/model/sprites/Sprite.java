package model.sprites;

import java.io.Serializable;


public interface Sprite extends Cloneable, Serializable {    
    public int getId();
    public Object clone();
    public Long getTimestamp();
    public Sprite nextPosition(Long simTime);
}
