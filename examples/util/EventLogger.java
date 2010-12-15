package util;

import org.apache.log4j.Logger;

import deism.core.Event;
import deism.core.EventDispatcher;
import deism.core.EventSink;

public class EventLogger implements EventDispatcher, EventSink {
    private final static Logger logger = Logger.getLogger(EventLogger.class);

    @Override
    public void dispatchEvent(Event event) {
        logger.info("Dispatch: " + event);
    }

    @Override
    public void offer(Event event) {
        logger.info("Offer: " + event);
    }

    @Override
    public void start(long startSimtime) {
    }

    @Override
    public void stop() {
    }
}
