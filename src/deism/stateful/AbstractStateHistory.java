package deism.stateful;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public abstract class AbstractStateHistory<K, V> implements StateHistory<K> {
    private final LinkedHashMap<K,Integer> snapshots =
        new LinkedHashMap<K,Integer>();
    private final List<V> history = new ArrayList<V>();
    
    @Override
    public void save(K key) throws StateHistoryException {
        if (snapshots.containsKey(key)) {
            throw new StateHistoryException("Key exists");
        }
        
        Integer nextIndex = history.size();
        snapshots.put(key, nextIndex);
    }

    @Override
    public void commit(K key) throws StateHistoryException {
        if (!snapshots.containsKey(key)) {
            throw new StateHistoryException("Key does not exist");
        }
        
        Integer nextIndex = snapshots.get(key);
        List<V> head = history.subList(0, nextIndex);
        int headSize = head.size();
        head.clear();
        
        // loop thru snapshots and rewrite index-values
        Set<K> expiredSnapshots = new HashSet<K>();
        for (Map.Entry<K, Integer> e : snapshots.entrySet()) {
            int newNextIndex = e.getValue() - headSize;
            if (newNextIndex < 0) {
                expiredSnapshots.add(e.getKey());
            }
            else {
                e.setValue(newNextIndex);
            }
        }
        snapshots.keySet().removeAll(expiredSnapshots);
    }

    @Override
    public void rollback(K key) throws StateHistoryException {
        if (!snapshots.containsKey(key)) {
            throw new StateHistoryException("Key does not exist");
        }
        
        Integer nextIndex = snapshots.get(key);
        List<V> tail = history.subList(nextIndex, history.size());
        revertHistory(tail);
        tail.clear();
        
        // loop thru snapshots and remove the ones beyond key
        boolean beyond = false;
        for (Iterator<K> it = snapshots.keySet().iterator(); it.hasNext(); ) {
            K currentKey = it.next();
            if (beyond) {
                it.remove();
            }
            if (currentKey.equals(key)) {
                beyond = true;
            }
        }
    }
    
    /**
     * During a rollback AbstractStateHistory splits off the items following
     * the rollback key in the history stack and calls revertHistory giving the
     * subclass a chance to restore state.
     * 
     * The first element of tail represents the state for rollback key
     * 
     * @param tail
     */
    public abstract void revertHistory(List<V> tail);
    
    /**
     * Push one item onto the history stack
     * 
     * @param item
     */
    public void pushHistory(V item) {
        assert(item != null);
        history.add(item);
    }
}
