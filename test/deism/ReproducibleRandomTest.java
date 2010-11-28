package deism;

import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ReproducibleRandomTest {
    @Mock Random rng;
    
    @Test
    public void testNextDouble() {
        ReproducibleRandom<Integer> r = new ReproducibleRandom<Integer>(rng);
        when(rng.nextDouble()).thenReturn(1.0);
        
        assertEquals(1.0, r.nextDouble(), 0);
        
        verify(rng).nextDouble();
    }
    
    @Test
    public void testSnapshotAtInitialState() {
        ReproducibleRandom<Integer> r = new ReproducibleRandom<Integer>(rng);
        
        r.save(1);
        r.commit(1);
        r.rollback(1);
    }
    
    /*
    @Test(expected=StateHistoryException.class)
    public void testSnapshotKeyMustBeUnique() {
        ReproducibleRandom<Integer> r = new ReproducibleRandom<Integer>(rng);
        r.save(1);
        r.save(1);
    }
    */

    @Test(expected=StateHistoryException.class)
    public void testCommitWithUnknownKeyMustFail() {
        ReproducibleRandom<Integer> r = new ReproducibleRandom<Integer>(rng);
        r.commit(1);
    }

    @Test(expected=StateHistoryException.class)
    public void testRollbackToUnknownKeyMustFail() {
        ReproducibleRandom<Integer> r = new ReproducibleRandom<Integer>(rng);
        r.rollback(1);
    }

    @Test
    public void testSnapshotRollback() {
        ReproducibleRandom<Integer> r = new ReproducibleRandom<Integer>(rng);
        when(rng.nextDouble()).thenReturn(1.0, 2.0, 3.0, 0.0);
        
        assertEquals(1.0, r.nextDouble(), 0);
        r.save(1);
        
        assertEquals(2.0, r.nextDouble(), 0);
        
        // try to rollback twice to the same value.
        r.rollback(1);
        assertEquals(2.0, r.nextDouble(), 0);

        r.rollback(1);
        assertEquals(2.0, r.nextDouble(), 0);
        assertEquals(3.0, r.nextDouble(), 0);
        
        // try to rollback once again to key=1
        r.rollback(1);
        assertEquals(2.0, r.nextDouble(), 0);
        r.save(2);
        assertEquals(3.0, r.nextDouble(), 0);
        
        // rollback to key=2
        r.rollback(2);
        assertEquals(3.0, r.nextDouble(), 0);        
        
        verify(rng, times(3)).nextDouble();
    }
    
    @Test
    public void testSnapshotCommit() {
        ReproducibleRandom<Integer> r = new ReproducibleRandom<Integer>(rng);
        when(rng.nextDouble()).thenReturn(1.0, 2.0, 3.0, 0.0);
        
        assertEquals(1.0, r.nextDouble(), 0);
        r.save(1);
        assertEquals(2.0, r.nextDouble(), 0);
        r.save(2);
        assertEquals(3.0, r.nextDouble(), 0);
        r.save(3);
        
        r.commit(2);
        
        try {
            r.rollback(1);
            fail("Rollback to states before commit must throw an exception");
        }
        catch(StateHistoryException e) {
            // expected
        }
        
        r.rollback(2);
                
        verify(rng, times(3)).nextDouble();
    }
}
