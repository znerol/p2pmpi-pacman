package wq1;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

import deism.AbstractGeneraterorEventSource;
import deism.Event;
import deism.EventDispatcher;
import deism.EventMatcher;
import deism.EventSource;
import deism.EventSourceCollection;
import deism.EventTimer;
import deism.FastForwardRunloop;
import deism.NoDelayEventTimer;
import deism.RealtimeClock;
import deism.RealtimeEventTimer;

public class JobQueueSimulation {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Random rng = new Random(1234);
		/* exit simulation after n units of simulation time */
		/* EventMatcher termCond = new TerminateAfterDuration(1000 * 100); */
		
		/* exit simulation after n events */
		EventMatcher termCond = new TerminateAfterEventcount(1000 * 100);
		
		/* run simulation as fast as possible */
		EventTimer eventTimer = new NoDelayEventTimer();
		
		/* run simulation in realtime */
		/*
		RealtimeClock clock = new RealtimeClock(1.0);
		EventTimer eventTimer = new RealtimeEventTimer(clock);
		*/
		
		FastForwardRunloop runloop = new FastForwardRunloop(eventTimer,
				termCond);
		
		PriorityQueue<ClientArrivedEvent> jobs =
			new PriorityQueue<ClientArrivedEvent>();
		
		EventSource[] sources = {
				new ClientArrivedSource(rng),
				new ClerkSource(jobs)
		};
		EventSource aggSource = new EventSourceCollection(sources);
		
		EventDispatcher disp = new JobAggregator(jobs);
		runloop.run(aggSource, disp);
	}
	
	/**
	 * TerminateAfterDuration.match will return true after given amount of
	 * simulation time elapsed.
	 */
	private static class TerminateAfterDuration implements EventMatcher {
		long duration;
		
		public TerminateAfterDuration(long duration) {
			this.duration = duration;
		}
		@Override
		public boolean match(Event e) {
			return (e.getSimtime() > duration);
		}
		
	}
	
	/**
	 * TerminateAfterEventcount.match will return true after a given number
	 * of events were dispatched.
	 */
	private static class TerminateAfterEventcount implements EventMatcher {
		int remaining;
		
		public TerminateAfterEventcount(int count) {
			remaining = count;
		}
		
		@Override
		public boolean match(Event e) {
			return (--remaining < 0);
		}
	}
	
	private static class JobAggregator implements EventDispatcher {
		Queue<ClientArrivedEvent> waitingQueue;
		
		public JobAggregator(Queue<ClientArrivedEvent> events) {
			waitingQueue = events;
		}
		
		@Override
		public void dispatchEvent(Event e) {
			System.out.println(e);
			if (e instanceof ClientArrivedEvent) {
				waitingQueue.offer((ClientArrivedEvent)e);
			}
			System.out.println("Queue Length: " + waitingQueue.size());
		}
	}
	
	private static class ClientArrivedEvent extends Event {
		long serviceTime;
		
		public ClientArrivedEvent(long arrivalTime, long serviceTime){
			super(arrivalTime);
			this.serviceTime = serviceTime;
		}
		
		long getServiceTime() {
			return serviceTime;
		}
		
		@Override
		public String toString() {
			return "[ClientArrivedEvent arrivalTime=" + this.getSimtime() + " serviceTime="
				+ serviceTime + "]";
		}
	}
	
	private static class ClerkFreeEvent extends Event {
		public ClerkFreeEvent(long simtime) {
			super(simtime);
		}
		
		@Override
		public String toString() {
			return "[ClerkFreeEvent time=" + this.getSimtime() + "]";
		}
	}
	
	private static class ClientArrivedSource
	extends AbstractGeneraterorEventSource {
		final long MEAN_MILLISECONDS_BETWEEN_JOBS = 1000;
		final long MEAN_MILLISECONDS_SERVICE_TIME = 800;
		final Random rng;
		
		public ClientArrivedSource(Random rng) {
			super();
			this.rng = rng;
		}
		
		@Override
		public Event nextEvent() {
			long arrivalTime = getLastEventSimtime() + (long)(
					MEAN_MILLISECONDS_BETWEEN_JOBS *
					-Math.log(rng.nextDouble()));
			long serviceTime = (long)(MEAN_MILLISECONDS_SERVICE_TIME *
					-Math.log(rng.nextDouble()));
			return new ClientArrivedEvent(arrivalTime, serviceTime);
		}
	}
	
	private static class ClerkSource extends AbstractGeneraterorEventSource
	{
		Queue<ClientArrivedEvent> jobs;
		
		public ClerkSource(Queue<ClientArrivedEvent> jobs)
		{
			super();
			this.jobs = jobs;
		}
		
		@Override
		public Event nextEvent() {
			ClientArrivedEvent job = jobs.poll();
			if (job == null) {
				return null;
			}
			long nextClerkFreeTime = job.getServiceTime()
				+ Math.max(getLastEventSimtime(), job.getSimtime());
			return new ClerkFreeEvent(nextClerkFreeTime);
		}
	}
}
