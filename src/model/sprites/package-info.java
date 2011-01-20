/**
 * Simulation sprites and states
 * 
 * <h3>Sprite</h3>
 * <p>
 * A sprite is a generic object that is on the board an has its own state.
 * The only thing a sprite has to do is to manage its state and to interact with 
 * the simulation framework. For this, a sprites implements the {@link deism.process.DiscreteEventProcess}
 * </p>
 * 
 * <h3>Sprite States</h3>
 * <p>
 * A sprite state represents a state and gives the behaviour to a sprite. 
 * Depending on which state is used, the actions and events are different. A 
 * state is every time associated with a sprite and a simulation time. A state
 * handels the events and performs the sprite specific action.  
 * </p>
 */

package model.sprites;