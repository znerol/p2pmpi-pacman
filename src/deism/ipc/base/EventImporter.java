package deism.ipc.base;

import deism.core.Event;

/**
 * An EventImporter is given the chance to inspect and alter incomming events.
 *
 * @see deism.tqgvt.Client
 */

public interface EventImporter {
    /**
     * Convert an event coming from another process into a local form.
     * 
     * @param event in external form
     * @return event in an local form
     */
    Event unpack(Event event);
}
