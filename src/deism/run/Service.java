package deism.run;

import java.util.ArrayList;
import java.util.List;

import deism.core.Startable;

public class Service implements Startable {
    private final List<Startable> startableList = new ArrayList<Startable>();

    public void addStartable(Startable startable) {
        startableList.add(startable);
    }

    @Override
    public void start(long simtime) {
        for (Startable startable : startableList) {
            startable.start(simtime);
        }
    }

    @Override
    public void stop(long simtime) {
        for (Startable startable : startableList) {
            startable.stop(simtime);
        }
    }
}
