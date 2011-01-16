package deism.ipc.base;

import deism.core.Event;

/**
 * An EventExporter has the chance to intercept and modify events leaving  for
 * another simulation island.
 * 
 * @see deism.tqgvt.Client
 */
public interface EventExporter {
    /**
     * Prepares an event for externalization to a remote process.
     *
     * @param event in local form
     * @return event in an exportable form
     */
    Event pack(Event event);
}
