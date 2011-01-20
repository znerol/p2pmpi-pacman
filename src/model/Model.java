package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import model.sprites.GhostState;
import model.sprites.PacmanState;
import model.sprites.Sprite;
import deism.core.Event;

public class Model {
    private final Board board;
    private final Random random;
    private static Model model;
    private final int clientCount;
    private final Set<DispatchedListener> listeners = new HashSet<DispatchedListener>();
    private final List<Sprite> sprites;
    private final static int MAX_PLAYER_COUNT = 4;
    
    public static Model getModel() {
        return model;
    }
    
    public Model(char[][] boardDef, int clientCount, long randomSeed) {
        assert (Model.model != null);
        assert (clientCount > 0);
        assert (clientCount <= MAX_PLAYER_COUNT);
        
        this.random = new Random(randomSeed);
        Model.model = this;
        this.clientCount = clientCount;
        this.board = new Board(boardDef);
        sprites = populateSpites(boardDef);
    }
    
    public void addDispatchedListener(DispatchedListener listener) {
        this.listeners.add(listener);
    }
    
    public void eventDispatched(Sprite sprite, Event event) {
        for (DispatchedListener listener : listeners) {
            listener.eventDispatched(new EventDispatchedEvent(sprite, event));
        }
    }
    
    public Direction getRandomDirection(int x, int y, Direction currentDirection) {
        List<Direction> dirs = getBoard().getWaypoint(x, y).getPossibleDirections();
        
        if (dirs.size() > 1)
            dirs.remove(currentDirection.inverse());
        
        int index = this.random.nextInt(dirs.size());
        return dirs.get(index);
    }
    
    private List<Sprite> populateSpites(char[][] boardDef) {        
        int id = 0;
        List<Sprite> sprites = new ArrayList<Sprite>();
        Sprite sprite = null;
        int clientCount = 0;
        for (int y = 0; y < boardDef.length; y++) {
            for (int x = 0; x < boardDef[y].length; x++) {
                if (clientCount < this.clientCount && boardDef[y][x] >= '0' && boardDef[y][x] <= '9') {
                    sprite = createPacman(x, y, id++);
                    sprites.add(sprite);
                    addDispatchedListener(sprite);
                    clientCount++;
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
    
    private Sprite createPacman(int x, int y, int id) {
        Waypoint centre = ((StreetSegment)board.getSegment(x, y)).getWaypointCentre();
        return new Sprite(new PacmanState(Direction.East, Direction.West, centre, id));
    }
    
    private Sprite createGhost(int x, int y, int id) {
        Waypoint centre = ((StreetSegment)board.getSegment(x, y)).getWaypointCentre();
        return new Sprite(new GhostState(Direction.East, Direction.West, centre, id));
    }
    
    public Board getBoard() {
        return this.board;
    }
    
    public List<Sprite> getSprites() {
        return this.sprites;
    }
    
    public Sprite getSprite(int id) {
        return this.sprites.get(id);
    }
}
