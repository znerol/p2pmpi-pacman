package deism;

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
        addPending(tail);
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
    
    public abstract void addPending(List<V> pending);
    
    public void addToHistory(V item) {
        history.add(item);
    }
    
    public void removeFromHistory(V item) {
        history.remove(item);
    }
}
