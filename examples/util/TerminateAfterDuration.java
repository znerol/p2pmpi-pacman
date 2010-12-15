package util;

import deism.core.Event;
import deism.core.EventCondition;

/**
 * TerminateAfterDuration.match will return true after given amount of
 * simulation time elapsed.
 */
public class TerminateAfterDuration implements EventCondition {
    long duration;

    public TerminateAfterDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public boolean match(Event e) {
        boolean result = false;
        if (e != null) {
            result = (e.getSimtime() > duration);
        }
        return result;
    }
}
