package deism.tqgvt;

import deism.core.Message;

public class GvtMessage implements Message {
    private static final long serialVersionUID = -7073545460339405226L;
    private final long gvt;

    public GvtMessage(long gvt) {
        this.gvt = gvt;
    }

    public long getGvt() {
        return gvt;
    }
}
