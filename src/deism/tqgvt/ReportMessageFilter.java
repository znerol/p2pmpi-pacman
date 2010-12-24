package deism.tqgvt;

import deism.ipc.base.Condition;
import deism.ipc.base.Message;

public class ReportMessageFilter implements Condition<Message> {
    @Override
    public boolean match(Message message) {
        return message instanceof ReportMessage;
    }
}
