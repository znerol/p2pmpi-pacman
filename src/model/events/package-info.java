/**
 * Pacman simulation events
 * 
 * <h3>Pacman simulation events</h3>
 * <p>
 * This events implements the concret events within the model. All four events
 * are necessary for this implementation.
 * </p>
 * 
 * <h2>Collision Event</h2>
 * <p>
 * Predicts a collision of two sprite. One of these sprites has to be at least
 * a pacman since ghosts can criss cross each other without problems.
 * </p>
 * 
 * <h2>Direction Event</h2>
 * <p>
 * Predicts a direction change of a sprite. This is only necessary for Pacmans
 * since the direction change of a ghost is a simply action that can be performed
 * by the simulation without events. The keyboard events are translated to 
 * direction events as well.
 * </p>
 * 
 * <h2>Enter Junction Event</h2>
 * <p>
 * Predicts the entrance of a sprite into a junction waypoint. There the sprite
 * has to decide where to go. 
 * </p>
 * 
 * <h2>Change View Event</h2>
 * <p>
 * Every time a sprite access an other street segment, it can change its view and
 * see other sprites. This happens if a street segment is entered or leaved which 
 * is a junction. On this time, the ghosts has to look around and check if they
 * see a pacman to case. 
 * </p>
 */

package model.events;