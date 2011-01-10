package deism.adapter;

import deism.core.Event;
import deism.core.EventSink;
import deism.ipc.base.EventExporter;

public class ExternalEventSinkAdapter implements EventSink {
    private final EventSink sink;
    private final EventExporter exporter;

    public ExternalEventSinkAdapter(EventSink sink, EventExporter exporter) {
        this.sink = sink;
        this.exporter = exporter;
    }

    @Override
    public void offer(Event event) {
        event = exporter.pack(event);

        if (event != null) {
            sink.offer(event);
        }
    }
}
