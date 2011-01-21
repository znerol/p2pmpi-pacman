package model;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Random;

import deism.stateful.AbstractStateHistory;

public class ReproducibleRandom<K> extends AbstractStateHistory<K, Integer> {
    private final Random rng;
    private final ArrayDeque<Integer> pending;

    public ReproducibleRandom(Random rng) {
        this.rng = rng;
        this.pending = new ArrayDeque<Integer>();
    }

    @Override
    public void revertHistory(List<Integer> tail) {
        this.pending.addAll(tail);
    }

    public int nextInt(int n) {
        Integer next = pending.poll();

        if (next == null) {
            next = new Integer(rng.nextInt(n));
        }
        
        pushHistory(next);
        return next;
    }
}
