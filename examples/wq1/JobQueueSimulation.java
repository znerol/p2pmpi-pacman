package wq1;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

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
		
		EventSource jobSource = new ClientArrivedSource(rng);
		EventSource clerkSource = new ClerkSource(jobs);
		
		List<EventSource> sources = new ArrayList<EventSource>();
		sources.add(jobSource);
		sources.add(clerkSource);
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
		Queue<ClientArrivedEvent> collectedEvents;
		long queuelength;
		
		public JobAggregator(Queue<ClientArrivedEvent> events) {
			collectedEvents = events;
			queuelength = 0;
		}
		
		@Override
		public void dispatchEvent(Event e) {
			System.out.println(e);
			if (e instanceof ClientArrivedEvent) {
				collectedEvents.offer((ClientArrivedEvent)e);
				queuelength++;
			}
			else if(e instanceof ClerkFreeEvent) {
				queuelength--;
			}
			System.out.println("Queue Length: " + queuelength);
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
	
	private static class ClientArrivedSource implements EventSource {
		final long MEAN_MILLISECONDS_BETWEEN_JOBS = 1000;
		final long MEAN_MILLISECONDS_SERVICE_TIME = 800;
		final Random rng;
		long lastArrivalTime = 0;
		Event currentEvent = null;
		
		public ClientArrivedSource() {
			this.rng = new Random();
		}
		
		public ClientArrivedSource(Random rng) {
			this.rng = rng;
			this.currentEvent = null;
		}
		
		@Override
		public Event peek() {
			if (currentEvent == null) {
				long arrivalTime = lastArrivalTime + (long)(
					MEAN_MILLISECONDS_BETWEEN_JOBS * -Math.log(rng.nextDouble()));
				long serviceTime = (long)(MEAN_MILLISECONDS_SERVICE_TIME *
						-Math.log(rng.nextDouble()));
				currentEvent = new ClientArrivedEvent(arrivalTime, serviceTime);
				lastArrivalTime = arrivalTime;
			}
			
			return currentEvent;
		}

		@Override
		public Event poll() {
			Event e = currentEvent;
			currentEvent = null;
			return e;
		}
	}
	
	private static class ClerkSource implements EventSource
	{
		Queue<ClientArrivedEvent> jobs;
		ClientArrivedEvent currentClient;
		ClerkFreeEvent currentClerkFreeEvent;
		long lastClerkFreeTime = 0;
		
		public ClerkSource(Queue<ClientArrivedEvent> jobs)
		{
			this.jobs = jobs;
		}
		
		@Override
		public Event peek() {
			if (currentClient == null) {
				currentClient = jobs.poll();
				if (currentClient == null) {
					currentClerkFreeEvent = null;
					return null;
				}
				long nextClerkFreeTime =
					Math.max(lastClerkFreeTime, currentClient.getSimtime())
					+ currentClient.getServiceTime();
				currentClerkFreeEvent = new ClerkFreeEvent(nextClerkFreeTime);
				lastClerkFreeTime = nextClerkFreeTime;
			}
			return currentClerkFreeEvent;
		}

		@Override
		public Event poll() {
			Event e = currentClerkFreeEvent;
			currentClerkFreeEvent = null;
			currentClient = null;
			return e;
		}
	}
}
