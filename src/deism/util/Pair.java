package deism.util;

import java.io.Serializable;

/**
 * Helper class for the model. Pairs two values of type A and B
 * 
 * @param <A>
 *            Type of the first value
 * @param <B>
 *            Type of the second value
 */
@SuppressWarnings("serial")
public class Pair<A, B> implements Cloneable, Serializable {
    public final A a;
    public final B b;
    
    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }
    
    @Override
    public String toString() {
        return "("+ (a==null?"":a.toString())+", "+(b==null?"":b.toString())+")";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((a == null) ? 0 : a.hashCode());
        result = prime * result + ((b == null) ? 0 : b.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Pair))
            return false;
        @SuppressWarnings("rawtypes")
        Pair other = (Pair) obj;
        if (a == null) {
            if (other.a != null)
                return false;
        } else if (!a.equals(other.a))
            return false;
        if (b == null) {
            if (other.b != null)
                return false;
        } else if (!b.equals(other.b))
            return false;
        return true;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        return new Pair<A, B>(a, b);
    }
}
