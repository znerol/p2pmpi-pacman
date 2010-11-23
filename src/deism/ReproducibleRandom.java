package deism;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Random;

public class ReproducibleRandom<K> extends AbstractStateHistory<K, Double> {
    private final Random rng;
    private final ArrayDeque<Double> pending;

    public ReproducibleRandom(Random rng) {
        this.rng = rng;
        this.pending = new ArrayDeque<Double>();
    }

    @Override
    public void addPending(List<Double> pending) {
        this.pending.addAll(pending);
    }

    public double nextDouble() {
        Double next = pending.poll();

        if (next == null) {
            next = new Double(rng.nextDouble());
        }
        
        addHistory(next);
        return next;
    }
}
