/**
 *  Deism - A Parallel Discrete Event Simulation Framework (PDES).
 *
 *  <p>
 *      Deism is a PDES framework featuring
 *      <ul>
 *          <li>a modular and reusable architecture</li>
 *          <li>optimistic synchronization of simulation islands based on
 *          jeffersons timewarp and TQ-GVT algorithms</li>
 *          <li>time scaling and different execution governors (realtime vs. as
 *          fast as possible)</li>
 *          <li>a universal event driven runloop</li>
 *          <li>integration with p2pmpi, other ipc mechanisms might be
 *          integrated easily</li>
 *      </ul>
 *  </p>
 *
 *  <h3>Basic Building Blocks</h3>
 *
 *  <h4>Event class</h4>
 *  <p>
 *      The {@link deism.core.Event} class essentially consists of a timestamp
 *      in simulation time units as well as a flag which is set if this event
 *      is an antimessage. In discrete event simulation event timestamps 
 *      normally lie in the future. Events are immutable data objects.
 *  </p>
 *
 *  <h4>EventSource interface</h4>
 *  <p>
 *      Each simulation consists of one or more {@link deism.core.EventSource}.
 *      The simulation {@link deism.run.Runloop} polls its EventSource for the
 *      Event with the smallest timestamp.
 *  </p>
 *  <p>
 *      An EventSource must ensure that it delivers the events in the proper
 *      order, i.e. increasing timestamp. If an EventSource does return an
 *      Event with a timestamp smaller than the current local virtual time, the
 *      runloop will attepmt to rollback to the timestamp of this Event.
 *  </p>
 *
 *  <h4>EventDispatcher interface</h4>
 *  <p>
 *      As soon as the the local virtual time of the simulation reaches the
 *      timestamp of the last event polled from the EventSource, the Event is
 *      passed to the {@link deism.core.EventDispatcher}.
 *  </p>
 *
 *  <h3>Process</h3>
 *
 *  <h4>DiscreteEventProcess interface</h4>
 *  <p>
 *      At least one EventSource and one EventDispatcher is required to model a
 *      discrete event simulation. The {@link
 *      deism.process.DiscreteEventProcess} groups those roles together into
 *      one interface.
 *  </p>
 *
 *  <h4>DefaultDiscreteEventProcess class</h4>
 *  <p>
 *      Because most discrete event simulations will consist of several sources
 *      and dispatchers, the {@link deism.process.DefaultDiscreteEventProcess}
 *      provides a convenient way to group together multiple sources and
 *      disptachers to one DiscreteEventProcess.
 *  </p>
 *
 *  <h4>DefaultProcessBuilder class</h4>
 *  <p>
 *      Putting together all the sources and dispatchers, probably wrapping
 *      them in adapters and registering them with services is a tedious job.
 *      That's where {@link deism.process.DefaultProcessBuilder} comes in
 *      handy. After adding all the elements defining the simulation using the
 *      overloaded {@code add} method the built DiscreteEventProcess can be
 *      retreived using the {@link
 *      deism.process.DefaultProcessBuilder#getProcess()}
 *  </p>
 *
 *  <h3>Runloop and Service</h3>
 *  <p>
 *      In order to maintain a simple structure of the {@link
 *      deism.run.Runloop#run()} method while providing a great amount of
 *      flexibility, some logic was extracted to supporting classes.
 *  </p>
 *
 *  <h4>ExecutionGovernor interface</h4>
 *  <p>
 *      The {@link deism.run.ExecutionGovernor} controlls execution timing,
 *      i.e. at which pace a simulation is executed. {@link
 *      deism.run.ImmediateExecutionGovernor} is a minimal implementation which
 *      drives a simulation as fast as possible while {@link
 *      RealtimExecutionGovernor} runs a process relative to the wallclock
 *      time.
 *  </p>
 *
 *  <h4>StateController interface</h4>
 *  <p>
 *      When running a parallel discrete event simulation with optimistic
 *      synchronization, it is essential to be able to rollback the simulation
 *      to a previously recorded state. On the other hand, there is no need to
 *      record state if the process is a simple single-thread discrete event
 *      simulation. {@link deism.run.StateHistoryController} is used for the
 *      former case while {@link deism.run.NoStateController} for the latter.
 *      Both classes implement the {@link deism.run.StateController} interface.
 *  </p>
 *
 *  <h4>Service class</h4>
 *  <p>
 *      The {@link deism.run.Service} class is the central place where observer
 *      classes register in order to be notified when the runloop is started or
 *      stopped, when it is about to be suspended, when a new local virtual
 *      time is reached, when state must be recorded, committed or rolled back,
 *      and when an event is about to be exported to another process or
 *      imported into the current process. Observers are registered with 
 *      {@link deism.run.Service#register(Object object)} method.
 *  </p>
 *
 *  <h4>Runloop</h4>
 *  <p>
 *      The {@link deism.run.Runloop} class is where the whole magic happens. A
 *      simulation is started using {@link
 *      deism.run.Runloop#run(DiscreteEventProcess process)} method and is
 *      terminated when either the terminationCondition is met or when the
 *      {@link deism.run.Runloop#stop()} method is called.
 *  </p>
 *
 *  <h3>Timewarp and GVT Implementation</h3>
 *  <p>
 *      In this implementation of parallel discrete event simulation with
 *      optimistic synchronization jeffersons timewarp algorithm is supported
 *      with some extensions to the basic distributed event simulation
 *      described above.
 *  </p>
 *
 *  <p>
 *      The following types are only meaningfull in PDES.
 *  </p>
 *
 *  <h4>StateHistory interface and AbstractStateHistory class</h4>
 *  <p>
 *      The {@link deism.stateful.StateHistory} interface describes methods to
 *      save current state, rollback to previous states and forget (commit)
 *      about states which were recorded in the distant past.
 *  </p>
 *  <p>
 *      Stateful classes which must maintain their state history may profit
 *      from easy state management by subclassing {@link
 *      deism.stateful.AbstractStateHistory} which already implements the
 *      methods required by the StateHistory interface. Whenever the state
 *      changes in a subclass of AbstractStateHistory it must record the new
 *      state by calling {@link
 *      deism.stateful.AbstractStateHistory#pushHistory}. In the event
 *      of a rollback the abstract method {@link
 *      deism.stateful.AbstractStateHistory#revertHistory} is
 *      called in order to allow the subclass to restore its state.
 *  </p>
 *
 *  <h4>EventSink interface and TimewarpEventSinkAdapter class</h4>
 *  <p>
 *      Before the runloop tells its ExecutionGovernor to suspend, the current
 *      Event is announced to the {@link deism.core.EventSink}. Typically an
 *      EventSink simply forwards those Events to other simulation islands
 *      running in different threads or even on different hosts.
 *  </p>
 *  <p>
 *      When another Event with a lower timestamp is received while the
 *      ExecutionGovernor was waiting for the local virtual time to reach the
 *      timestamp of the current Event, a corresponding anti-Event is announced
 *      to the EventSink in order to cancel the previously announced Event.
 *  </p>
 *  <p>
 *      Luckily most of the time an EventSink just has to deliver Events to
 *      other simulation islands. Therefore it is easy to bundle the logic
 *      needed to save and restore state as well as annihilate Events with
 *      their anti-Event counterparts in one class called {@link
 *      deism.state.TimewarpEventSinkAdapter}.
 *  </p>
 *  <p>
 *      Note that {@link deism.process.DefaultProcessBuilder#add(EventSink
 *      sink)} will decorate a given EventSink automatically with a
 *      TimewarpEventSinkAdapter if it is annotated with the {@link
 *      deism.core.Stateful}.
 *   </p>
 *   <p>
 *      Furthermore EventSinks communicating to other simulation islands must
 *      be annotated with {@link deism.core.External}.  The
 *      DefaultProcessBuilder will wrap such EventSinks into {@link
 *      deism.adapter.ExternalEventSinkAdapter} which allows the interception
 *      of outgoing events. All the GVT alhorithms relly on information about
 *      transient Events. ExternalEventSinkAdapter gives the GVT a chance to
 *      act on and - if necessary - add additional information to Events
 *      leaving their simulation island.
 *  </p>
 *  <p>
 *      Refer to {@link deism.p2pmpi.MpiEventSink} for an example on how an
 *      EventSink might be implemented.
 *  </p>
 *
 *  <h4>TimewarpEventSourceAdapter class</h4>
 *  <p>
 *      Analogous to the TimewarpEventSinkAdapter this class is responsible to
 *      manage Events and anti-Events possibly comming from another simulation
 *      island. {@link deism.stateful.TimewarpEventSourceAdapter} implements
 *      the StateHistory interface and relieves the adapted EventSource to
 *      implement state management.
 *  </p>
 *  <p>
 *      Just like in the case of EventSource, DefaultProcessBuilder will
 *      decorate EventSource instances automatically with
 *      TimewarpEventSourceAdapter if the implementation is annotated with
 *      {@link deism.core.Stateful}. Also a StatefulEventGenerator annotated
 *      with {@link deism.core.External} will be wrapped into an {@link
 *      deism.adapter.ExternalEventSinkAdapter} by DefaultProcessBuilder in
 *      order to let the GVT algorithm to hook into retreival of Events from
 *      other simulation islands.
 *  </p>
 *  <p>
 *      Refer to {@link deism.p2pmpi.MpiEventGenerator} for an example on how
 *      an EventSource (in this case StatefulEventGenerator} looks like.
 *  </p>
 *
 *  <h4>IPC Message System</h4>
 *  <p>
 *      {@link deism.ipc.base.Message} is used to coordinate execution among
 *      different simulation islands. All pending messages are processed by the
 *      runloop before the EventSource is polled. They get dispatched
 *      immediately and are not scheduled for later delivery like Events.
 *  </p>
 *  <p>
 *      Messages are generated by instances of classes implementing {@link
 *      deism.ipc.base.Emitter}. Each emitter must be connected to an {@link
 *      deism.ipc.base.Endpoint} before the first message is sent. Upon
 *      reception of a Message an Endpoint either queues it or passes it to one
 *      or more {@link deism.ipc.base.MessageHandler}.
 *  </p>
 *  <p>
 *      The {@link deism.run.MessageCenter} simplifies message handling by
 *      providing a simple api to register and interconnect MessageEmitters,
 *      MessageHandlers and MessageEndpoints.
 *  </p>
 *
 *  <h4>TQ-GVT alhorithm</h4>
 *  <p>
 *      Based on the ipc message system and on {@link deism.ipc.EventExporter}
 *      and {@link deism.ipc.EventImporter} we implemented a version of the
 *      time quantum GVT algorithm. TQ-GVT consists of a {@link
 *      deism.tqgvt.Master} collecting local virtual time and information on
 *      transient events from several {@link deism.tqgvt.Client}. Based on
 *      those reports the master periodically calculates new values for the
 *      global virtual time and, if the value changed, broadcasts it back to
 *      its clients.
 *  </p>
 *  <p>
 *      Each client divides its wallclock time into time quantums of a fixed
 *      length (typically 100ms). When an event leaves the simulation island,
 *      the TQ-GVT client appends its current time quantum value to the event
 *      and increments a counter for the number of messages sent during the
 *      current time quantum. On the other side the TQ-GVT client receiving the
 *      event strips off the time quantum appended to the event by the sender
 *      and increments a counter in a map indexed by the originating time
 *      quantum values. Whenever a client enters a new time quantum the current
 *      lvt and the send, as well as the receive counters are reported to the
 *      TQ-GVT master.
 *  </p>
 *
 *  <h3>Review and Reflection</h3>
 *  <p>
 *      We think that the careful design of the deism framework resulted in a
 *      robust and reusable piece of software. A large amount of code is
 *      covered by automated unit tests providing the necessary safety net when
 *      working with complex problems.
 *  </p>
 *
 *  <p>
 *      There is still room for improvement though. Especially in the following
 *      domains:
 *
 *      <ul>
 *          <li><b>GVT algorithms:</b> It would be very interesting to
 *          implement and compare the characteristics of various GVT
 *          algorithms. Because of the modular implementation of the deism
 *          framework it should be quite easy to add some more GVT algorithms
 *          like CM GVT (Continously Monitored Global Virtual Time).
 *          <li><b>IPC backends:</b> Substituting P2P-MPI with another, more
 *          mature MPI system might increase the look and feel of the
 *          system. That's speculation though.</li>
 *          <li><b>Naming:</b> The very different roles of EventSink and
 *          EventDispatcher are not obvious at the first glance. A better name
 *          for one or the other component might improve the situation.</li>
 *          <li><b>Separation of Concerns:</b> The Service class is anything to
 *          anyone. At least the EventImporter and EventExporter should be
 *          splitted off into a new class because Service acts as a delegate in
 *          this case. In all the other cases Service implements the observer
 *          pattern.</li>
 *          <li><b>Performance:</b> We do not have any numbers about the
 *          runtime performance of our system. We know it works but we don't
 *          know if it works fast enough.</li>
 *      </ul>
 *  </p>
 *
 *  <h3>Ressources</h3>
 *  <ul>
 *      <li><a href="ftp://ftp.cs.ucla.edu/tech-report/198_-reports/890060.pdf">Virtual Time II</a>:
 *      The Cancelback Protocol for Storage Management in Time Warp by David
 *      Jefferson, Computer Science Department Technical Report, CSD-890060,
 *      November 1989</li>
 *      <li><a href="http://www.cs.rpi.edu/~szymansk/papers/pdpta.97.ewa.pdf">CM GVT</a>
 *      Continously Monitored Global Virtual Time by Ewa Deelman and Boleslaw
 *      K. Szymanski, Proc. Int. Conference on Parallel and Distributed
 *      Processing Techniques and Applications (PDPTA'97), Las Vegas, NV, June
 *      30-July 3, 1997, Vol. I, pp. 1-10</li>
 *      <li><a href="http://www.merl.com/reports/docs/TR2000-17.pdf">Timewarp Rigid Body Simulation</a>
 *      by Brian Mirtich, MERL Mitsubishi Electric Research Lab, TR2000-17,
 *      December 2000</li>
 *      <li><a href="http://www.cs.rpi.edu/~szymansk/papers/scpe.07.pdf">Time Quantum GVT</a>:
 *      A Scalable Computation of the Global Virtual Time in Parallel Discrete
 *      Event Simulations by Gilbert G. Chen And Boleslaw K.  Szymanski,
 *      Scalable Computing: Practice and Experience, Vol. 8, No. 4, 2008, pp.
 *      423-435</li>
 *  </ul>
 *
 *  @author Lorenz Schori <a href="mailto:schol2@bfh.ch">schol2@bfh.ch</a>
 *  @author Ruben B&auml;r <a href="mailto:barbr2@bfh.ch">barbr2@bfh.ch</a>
 */
package deism;
