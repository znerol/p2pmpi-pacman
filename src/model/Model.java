package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import model.events.VisitableEvent;
import model.sprites.Ghost;
import model.sprites.Pacman;
import model.sprites.Sprite;
import deism.core.Event;
import deism.process.DiscreteEventProcess;
import deism.stateful.AbstractStateHistory;

public class Model extends AbstractStateHistory<Long, GameState> implements DiscreteEventProcess {
    private final Board board;
    private GameState currentState;
    private Event currentEvent;
    private int pacmanCount;
    private final Random random;
    private static Model model;
    
    public static Model getModel() {
        return model;
    }
    
    public Model(char[][] boardDef, int pacmanCount, long randomSeed) {
        assert (Model.model != null);
        
        this.random = new Random(randomSeed);
        Model.model = this;
        this.board = new Board(boardDef);
        Collection<Sprite> sprites = populateSpites(boardDef, pacmanCount);
        
        currentState = new GameState(sprites.toArray(new Sprite[0]));
    }
    
    public Direction getRandomDirection(int x, int y) {
        List<Direction> dirs = getBoard().getWaypoint(x, y).getPossibleDirections();
        int index = this.random.nextInt(dirs.size());
        return dirs.get(index);
    }
    
    private Collection<Sprite> populateSpites(char[][] boardDef, int maxPacmanCount) {
        int id = 0;
        Collection<Sprite> sprites = new ArrayList<Sprite>();
        
        for (int y = 0; y < boardDef.length; y++) {
            for (int x = 0; x < boardDef[y].length; x++) {
                if (this.pacmanCount < maxPacmanCount && boardDef[y][x] >= '0' && boardDef[y][x] <= '9')
                    sprites.add(createPacman(x, y, id++));
                else if (boardDef[y][x] >= 'a' && boardDef[y][x] <= 'g')
                    sprites.add(createGhost(x, y, id++));
                else if (boardDef[y][x] == 's')
                    continue; // Happy Pill
                else
                    continue; // Points
            }
        }
        
        return sprites;
    }
    
    private Pacman createPacman(int x, int y, int id) {
        Waypoint centre = ((StreetSegment)board.getSegment(x, y)).getWaypointCentre();
        return new Pacman(Direction.East, Direction.East, centre, id);
    }
    
    private Ghost createGhost(int x, int y, int id) {
        Waypoint centre = ((StreetSegment)board.getSegment(x, y)).getWaypointCentre();
        return  new Ghost(Direction.East, Direction.East, centre, id);
    }
    
    public Board getBoard() {
        return this.board;
    }
    
    public Sprite[] getSprites() {
        return this.currentState.getSprites();
    }
    
    public Sprite getSprite(int id) {
        return this.currentState.getSprite(id);
    }
    
    public GameState getCurrentState() {
        return this.currentState;
    }

    @Override
    public Event peek(long currentSimtime) {
        if (currentEvent != null)
            return currentEvent;
        return null;
    }

    @Override
    public void remove(Event event) {
        currentEvent = null;
    }

    @Override
    public void offer(Event event) {
        // not used
    }

    @Override
    public void dispatchEvent(Event e) {
        if (e instanceof VisitableEvent) {
            VisitableEvent ve = (VisitableEvent)e;
            currentState = new GameState(currentState);
            ve.accept(currentState);
            pushHistory(currentState);
            currentEvent = currentState.getEvent();
        }
    }

    @Override
    public void revertHistory(List<GameState> tail) {
        if (tail.size() > 0) {
            currentState = tail.get(0);
            currentEvent = currentState.getEvent();
        }
    }
}
