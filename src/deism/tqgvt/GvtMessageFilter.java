package deism.tqgvt;

import deism.ipc.base.Condition;
import deism.ipc.base.Message;

public class GvtMessageFilter implements Condition<Message> {
    @Override
    public boolean match(Message message) {
        return message instanceof GvtMessage;
    }
}
