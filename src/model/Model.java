package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.sprites.GhostState;
import model.sprites.PacmanState;
import model.sprites.Sprite;
import deism.core.Event;

/**
 * Acts as decorator for the {@link model.sprites.SpriteState} and
 * {@link model.sprites.Sprite} models. Only one instance is allowed.
 */
public class Model {
    public final static int WAYPOINTS_PER_TILE = 9;
    private final Board board;
    private ReproducibleRandom<Long> random;
    private static Model model;
    private final int clientCount;
    private final Set<DispatchedListener> listeners = new HashSet<DispatchedListener>();
    private final List<Sprite> sprites;
    private final static int MAX_PLAYER_COUNT = 4;

    /**
     * Returns the static model instance
     * 
     * @return Static model instance
     */
    public static Model getModel() {
        return model;
    }

    public Model(char[][] boardDef, int clientCount) {
        assert (Model.model == null);
        assert (clientCount > 0);
        assert (clientCount <= MAX_PLAYER_COUNT);

        Model.model = this;
        this.clientCount = clientCount;
        this.board = new Board(boardDef);
        sprites = populateSpites(boardDef);
    }
    
    /**
     * Sets a reproducible random generator for the simulation
     * @param random random generator
     */
    public void setRandomGenerator(ReproducibleRandom<Long> random) {
        assert (random != null);
        
        this.random = random;
    }

    /**
     * Registers a listener for dispatched events
     * 
     * @param listener
     *            Interested listener
     */
    public void addDispatchedListener(DispatchedListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Informs all registered {@link model.sprites.Sprite} isntances about a
     * dispached event.
     * 
     * @param sprite
     *            Dispaching sprite
     * @param event
     *            Dispatched event.
     */
    public void eventDispatched(Sprite sprite, Event event) {
        for (DispatchedListener listener : listeners) {
            listener.eventDispatched(new EventDispatchedEvent(sprite, event));
        }
    }

    /**
     * Keeps track to the random directions of the instance of the ghosts.
     * Prevents the ghosts from turning back if they are not in a dead end.
     * 
     * @param x
     *            Ghost's abscissa
     * @param y
     *            Ghost's ordinate
     * @param currentDirection
     *            current direction
     * @return next calculated direction
     */
    public Direction getRandomDirection(int x, int y, Direction currentDirection) {
        List<Direction> dirs = getBoard().getWaypoint(x, y)
                .getPossibleDirections();

        // If more than one option is possible, remove the inverse of the
        // current direction
        if (dirs.size() > 1)
            dirs.remove(currentDirection.inverse());

        int index = this.random.nextInt(dirs.size());
        return dirs.get(index);
    }

    /**
     * Instantiates and populates all {@link model.sprites.Sprite}s on the board
     * and assignes them all a unique id
     * 
     * @param boardDef
     *            Board definition
     * @return all created sprites
     */
    private List<Sprite> populateSpites(char[][] boardDef) {
        int id = 0;
        List<Sprite> sprites = new ArrayList<Sprite>();
        Sprite sprite = null;
        int clientCount = 0;
        for (int y = 0; y < boardDef.length; y++) {
            for (int x = 0; x < boardDef[y].length; x++) {
                // populate pac that are marked with numbers from 0-9
                if (clientCount < this.clientCount && boardDef[y][x] >= '0'
                        && boardDef[y][x] <= '9') {
                    sprite = createPacman(x, y, id++);
                    sprites.add(sprite);
                    addDispatchedListener(sprite);
                    clientCount++;
                    // populate the ghosts that are marked with a-g
                } else if (boardDef[y][x] >= 'a' && boardDef[y][x] <= 'g') {
                    sprite = createGhost(x, y, id++);
                    sprites.add(sprite);
                    addDispatchedListener(sprite);
                } else if (boardDef[y][x] == 's')
                    continue; // Happy Pill
                else
                    continue; // Points
            }
        }

        return sprites;
    }

    /**
     * Instantiates a {@link model.sprites.Sprite} as pacman at the given point
     * 
     * @param x
     *            abscissa
     * @param y
     *            ordinate
     * @param id
     *            unique id
     * @return new pacman instance
     */
    private Sprite createPacman(int x, int y, int id) {
        Waypoint centre = ((StreetSegment) board.getSegment(x, y))
                .getWaypointCentre();
        return new Sprite(new PacmanState(Direction.East, Direction.West,
                centre, id));
    }

    /**
     * Instantiates a {@link model.sprites.Sprite} as ghost at the given point
     * 
     * @param x
     *            abscissa
     * @param y
     *            ordinate
     * @param id
     *            unique id
     * @return new ghost instance
     */
    private Sprite createGhost(int x, int y, int id) {
        Waypoint centre = ((StreetSegment) board.getSegment(x, y))
                .getWaypointCentre();
        return new Sprite(new GhostState(Direction.East, Direction.West,
                centre, id));
    }

    /**
     * Board getter
     * 
     * @return the board instance
     */
    public Board getBoard() {
        return this.board;
    }

    /**
     * Sprite getter
     * 
     * @return all instantiated sprites
     */
    public List<Sprite> getSprites() {
        return this.sprites;
    }

    /**
     * Looks up for a sprite with the given id
     * 
     * @param id
     *            Sprite's unique id
     * @return Sprite with the unique id
     */
    public Sprite getSprite(int id) {
        return this.sprites.get(id);
    }
}
