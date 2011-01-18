package model.sprites;

import java.io.Serializable;


public interface Sprite extends Cloneable, Serializable {    
    public int getId();
    public Object clone() throws CloneNotSupportedException;
    public Long getTimestamp();
}
