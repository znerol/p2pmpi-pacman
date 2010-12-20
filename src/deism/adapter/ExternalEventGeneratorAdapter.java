package deism.adapter;

import deism.core.Event;
import deism.core.EventImporter;
import deism.core.StatefulEventGenerator;

public class ExternalEventGeneratorAdapter implements StatefulEventGenerator {
    private final StatefulEventGenerator generator;
    private final EventImporter importer;

    public ExternalEventGeneratorAdapter(StatefulEventGenerator generator,
            EventImporter importer) {
        this.generator = generator;
        this.importer = importer;
    }

    @Override
    public Event poll() {
        Event result = generator.poll();

        if (result != null) {
            result = importer.unpack(result);
        }

        return result;
    }
}
