package deism.util;

import java.io.Serializable;

/**
 * Helper class for the model. Binds three values of type A, B and C
 *  
 * @param <A>
 *            Type of the first value
 * @param <B>
 *            Type of the second value
 * @param <C>
 *            Type of the third value
 */
@SuppressWarnings("serial")
public class Triple<A, B, C> implements Cloneable, Serializable {
    public final A a;
    public final B b;
    public final C c;

    public Triple(A a, B b, C c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public Triple(A a, Pair<B, C> bc) {
        this.a = a;
        this.b = bc.a;
        this.c = bc.b;
    }

    public Triple(Pair<A, B> ab, C c) {
        this.a = ab.a;
        this.b = ab.b;
        this.c = c;
    }

    @Override
    public String toString() {
        return "(" + (a == null ? "" : a.toString()) + ", "
                + (b == null ? "" : b.toString()) + ", "
                + (c == null ? "" : c.toString()) + ")";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((a == null) ? 0 : a.hashCode());
        result = prime * result + ((b == null) ? 0 : b.hashCode());
        result = prime * result + ((c == null) ? 0 : c.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Triple))
            return false;
        @SuppressWarnings("rawtypes")
        Triple other = (Triple) obj;
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
        if (c == null) {
            if (other.c != null)
                return false;
        } else if (!c.equals(other.c))
            return false;
        return true;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new Triple<A, B, C>(a, b, c);
    }

}
