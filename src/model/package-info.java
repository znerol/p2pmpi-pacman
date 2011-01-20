/**
 * Pacman simulation model implementation
 * 
 * <h3>Tile Bases Board</h3>
 * <p>
 * The whole board in this simulation is divided into quadratic segment tiles. 
 * The board model represents the board that is initialised by a ASCII 
 * definition. The board distinguishes between wall segments and street segment.
 * Further, each of these tiles are doubled linked together, even on the edges.
 * This would make id easy to extend the simulation to pass one side and enter
 * the other again.
 * 
 *   A street segment can be a junction, which is a curve as well, or not. 
 * </p>
 * 
 * <h3>Waypoint Bases Board</h3>
 * <p>
 * There exits waypoints next to the tiles. Every StreetSegment contains one 
 * center waypoint and if necessary in every cardinal direction 4 more since 
 * the tiles are 9 width and 9 length. These values are just examples and are 
 * defined in the GamePlay class.
 * </p>
 * <p>
 * Every sprite can move on these waypoints and query them for different 
 * informations like wall distance, next interesting point etc. These interesting 
 * points are determined by two properties. First if they are a junction and 
 * sprites can take there an other direction or a view change waypoint. 
 * The view can change if a sprite enters or leaves a junction on segment level.
 * </p>
 */

package model;