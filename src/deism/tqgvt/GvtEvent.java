package deism.tqgvt;

import deism.core.Event;

public class GvtEvent extends Event {
    private static final long serialVersionUID = -7073545460339405226L;
    private final long gvt;

    public GvtEvent(long gvt) {
        super(0);
        this.gvt = gvt;
    }

    public long getGvt() {
        return gvt;
    }
}
