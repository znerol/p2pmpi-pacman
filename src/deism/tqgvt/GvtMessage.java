package deism.tqgvt;

import deism.ipc.base.Message;

/**
 * {@link Message} sent from {@link Master} to {@link Client} whenever global
 * virtual changes.
 */
public class GvtMessage implements Message {
    private static final long serialVersionUID = -7073545460339405226L;
    private final long gvt;

    public GvtMessage(long gvt) {
        this.gvt = gvt;
    }

    public long getGvt() {
        return gvt;
    }

    @Override
    public String toString() {
        return "[GvtMessage gvt=" + gvt + "]";
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        GvtMessage otherEvent = (GvtMessage) other;
        return this.gvt == otherEvent.gvt;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (int) (gvt ^ (gvt >>> 32));
        return hash;
    }

}
