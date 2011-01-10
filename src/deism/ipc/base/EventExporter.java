package deism.ipc.base;

import deism.core.Event;

public interface EventExporter {
    /**
     * Prepares an event for externalization to a remote process.
     *
     * @param event in local form
     * @return event in an exportable form
     */
    Event pack(Event event);
}
