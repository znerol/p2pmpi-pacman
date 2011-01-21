package model;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Random;

import deism.stateful.AbstractStateHistory;

/**
 * Produces reproducible random numbers and implements a state history so that
 * the numbers will not diverge.
 * 
 * @param <K>
 *            History Key
 */
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

    /**
     * {@see java.util.Random} Random number with state history.
     */
    public int nextInt() { 
        Integer next = pending.poll();

        if (next == null) {
            next = new Integer(rng.nextInt());
        }

        pushHistory(next);
        return next;
    }
}
