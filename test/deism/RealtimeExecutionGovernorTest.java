package deism;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import deism.run.RealtimeExecutionGovernor;
import deism.run.SystemTimeProxy;
import deism.run.Timebase;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RealtimeExecutionGovernorTest {
    @Mock private Timebase simulationTimebase;
    @Mock private Timebase systemTimebase;
    @Mock private SystemTimeProxy systemTime;

    @InjectMocks private RealtimeExecutionGovernor governor =
        new RealtimeExecutionGovernor(1.0);

    private class GovernorSuspendCallable implements Callable<Long> {
        @Override
        public Long call() throws Exception {
            return governor.suspend();
        }
    }

    private class GovernorSuspendUntilCallable implements Callable<Long> {
        private final long wakeupTime;

        public GovernorSuspendUntilCallable(long wakeupTime) {
            this.wakeupTime = wakeupTime;
        }

        @Override
        public Long call() throws Exception {
            return governor.suspendUntil(wakeupTime);
        }
    }

    public void busyWaitForThreadState(Thread thread, Thread.State state)
            throws InterruptedException {
        int i = 0;
        while(thread.getState() != state) {
            if (i++ < 10) {
                Thread.sleep(10);
            }
            else {
                fail("Governor must suspend execution of its thread.");
            }
        }
    }

    @Test
    public void testStart() {
        when(systemTime.get()).thenReturn(7L);

        governor.start(42L);

        verify(systemTime).get();
        verify(systemTimebase).setTimebase(7L);
        verify(simulationTimebase).setTimebase(42L);

        verifyNoMoreInteractions(simulationTimebase);
        verifyNoMoreInteractions(systemTimebase);
        verifyNoMoreInteractions(systemTime);
    }

    @Test
    public void testStop() {
        // This is actually a no-op...
        governor.stop(0L);

        verifyNoMoreInteractions(simulationTimebase);
        verifyNoMoreInteractions(systemTimebase);
        verifyNoMoreInteractions(systemTime);
    }

    @Test
    public void testSuspendResume() throws Exception {
        // define a timestamp returned by clock and therefore by governor
        // suspend when wakeup is called
        when(systemTime.get()).thenReturn(7L);
        when(systemTimebase.convert(7L, simulationTimebase)).thenReturn(42L);

        // setup a future task wrapping governor.suspend
        RunnableFuture<Long> suspender = new FutureTask<Long>(
                new GovernorSuspendCallable());
        Thread thread = new Thread(suspender);
        thread.start();

        // wait until suspender got called and governor suspended the thread
        busyWaitForThreadState(thread, Thread.State.WAITING);

        // resume governor. This method is called when an EventSource running
        // in its own thread wants to get polled.
        governor.resume();

        // verify the result and the call to clock
        long result = suspender.get();
        assertEquals(42L, result);

        // called in resume
        verify(systemTime).get();
        verify(systemTimebase).convert(7L, simulationTimebase);

        verifyNoMoreInteractions(simulationTimebase);
        verifyNoMoreInteractions(systemTimebase);
        verifyNoMoreInteractions(systemTime);
    }

    @Test
    public void testSuspendUntilButContinueImmediately() throws Exception {
        // define a timestamp returned by clock and therefore by governor
        // suspend when wakeup is called
        when(systemTime.get()).thenReturn(10L);
        when(systemTimebase.convert(10L, simulationTimebase)).thenReturn(10L);

        // we don't expect governor.suspend(10L) to suspend (wait) at all
        // because the delay (clock.getRealtime(10L) - clock.getRealtime())
        // should be zero.
        long result = governor.suspendUntil(10L);
        assertEquals(10L, result);

        // called in suspendUntil
        verify(systemTime).get();
        verify(simulationTimebase).convert(10L, systemTimebase);

        verifyNoMoreInteractions(simulationTimebase);
        verifyNoMoreInteractions(systemTimebase);
        verifyNoMoreInteractions(systemTime);
    }

    @Test
    public void testSuspendUntilReturnAtTime() throws Exception {
        // define a timestamp returned by clock and therefore by governor
        // suspend when wakeup is called
        when(systemTime.get()).thenReturn(7L);
        when(simulationTimebase.convert(10L, systemTimebase)).thenReturn(10L);

        // we expect that the junit thread is delayed for 10 milliseconds.
        long result = governor.suspendUntil(10L);
        assertEquals(10L, result);

        // called in suspendUntil
        verify(systemTime).get();
        verify(simulationTimebase).convert(10L, systemTimebase);

        verifyNoMoreInteractions(simulationTimebase);
        verifyNoMoreInteractions(systemTimebase);
        verifyNoMoreInteractions(systemTime);
    }

    @Test
    public void testSuspendUntilResumeEarly() throws Exception {
        // define a timestamp returned by clock and therefore by governor
        // suspend when wakeup is called
        when(systemTime.get()).thenReturn(7L);
        when(systemTimebase.convert(7L, simulationTimebase)).thenReturn(42L);
        when(simulationTimebase.convert(1000L, systemTimebase)).thenReturn(1000L);

        // setup a future task wrapping governor.suspend
        RunnableFuture<Long> suspender = new FutureTask<Long>(
                new GovernorSuspendUntilCallable(1000L));
        Thread thread = new Thread(suspender);
        thread.start();

        // wait until suspender got called and governor suspended the thread
        busyWaitForThreadState(thread, Thread.State.TIMED_WAITING);

        // resume governor. This method is called when an EventSource running
        // in its own thread wants to get polled.
        governor.resume(10L);

        // verify the result and the call to clock
        long result = suspender.get();
        assertEquals(10L, result);

        // suspendUntil
        verify(simulationTimebase).convert(1000L, systemTimebase);
        // resume
        verify(systemTimebase).convert(7L, simulationTimebase);
        // suspendUntil / resume
        verify(systemTime, times(2)).get();

        verifyNoMoreInteractions(simulationTimebase);
        verifyNoMoreInteractions(systemTimebase);
        verifyNoMoreInteractions(systemTime);
    }

    @Test
    public void testSuspendUntilResumeEarlyClockWins() throws Exception {
        // define a timestamp returned by clock and therefore by governor
        // suspend when wakeup is called
        when(systemTime.get()).thenReturn(7L);
        when(systemTimebase.convert(7L, simulationTimebase)).thenReturn(10L);
        when(simulationTimebase.convert(1000L, systemTimebase)).thenReturn(1000L);

        // setup a future task wrapping governor.suspend
        RunnableFuture<Long> suspender = new FutureTask<Long>(
                new GovernorSuspendUntilCallable(1000L));
        Thread thread = new Thread(suspender);
        thread.start();

        // wait until suspender got called and governor suspended the thread
        busyWaitForThreadState(thread, Thread.State.TIMED_WAITING);

        // resume governor. This method is called when an EventSource running
        // in its own thread wants to get polled.
        governor.resume(42L);

        // verify the result and the call to clock
        long result = suspender.get();
        assertEquals(10L, result);

        // suspendUntil
        verify(simulationTimebase).convert(1000L, systemTimebase);
        // resume
        verify(systemTimebase).convert(7L, simulationTimebase);
        // suspendUntil / resume
        verify(systemTime, times(2)).get();

        verifyNoMoreInteractions(simulationTimebase);
        verifyNoMoreInteractions(systemTimebase);
        verifyNoMoreInteractions(systemTime);
    }
}
