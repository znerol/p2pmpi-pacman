package util;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Random;

import deism.stateful.AbstractStateHistory;

public class ReproducibleRandom<K> extends AbstractStateHistory<K, Double> {
    private final Random rng;
    private final ArrayDeque<Double> pending;

    public ReproducibleRandom(Random rng) {
        this.rng = rng;
        this.pending = new ArrayDeque<Double>();
    }

    @Override
    public void revertHistory(List<Double> tail) {
        this.pending.addAll(tail);
    }

    public double nextDouble() {
        Double next = pending.poll();

        if (next == null) {
            next = new Double(rng.nextDouble());
        }
        
        pushHistory(next);
        return next;
    }
}
