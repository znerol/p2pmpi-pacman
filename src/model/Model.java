package model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import model.events.ChangeViewEvent;
import model.events.CollisionEvent;
import model.events.DirectionEvent;
import model.events.EnterJunctionEvent;
import model.events.EventVisitor;
import model.sprites.Ghost;
import model.sprites.Pacman;
import model.sprites.Sprite;
import deism.core.Event;
import deism.process.DiscreteEventProcess;
import deism.stateful.AbstractStateHistory;

public class Model extends AbstractStateHistory<Long, GameState> implements DiscreteEventProcess {
    private final Board board;
    private final Map<Integer, Sprite> sprites = new HashMap<Integer, Sprite>();
    private final Map<Integer, Pacman> pacs = new HashMap<Integer, Pacman>();
    private final Map<Integer, Ghost> ghosts = new HashMap<Integer, Ghost>();
    private GameState currentState;
    private int pacmanCount;
    private static Model model;
    
    public static Model getModel() {
        return model;
    }
    
    public Model(char[][] boardDef, int pacmanCount) {
        Model.model = this;
        this.board = new Board(boardDef);
        populateSpites(boardDef, pacmanCount);
        
        currentState = new GameState(sprites.values().toArray(new Sprite[0]));
    }
    
    private void populateSpites(char[][] boardDef, int maxPacmanCount) {
        int id = 0;
        for (int y = 0; y < boardDef.length; y++) {
            for (int x = 0; x < boardDef[y].length; x++) {
                if (this.pacmanCount < maxPacmanCount && boardDef[y][x] >= '0' && boardDef[y][x] <= '9')
                    createPacman(x, y, id++);
                else if (boardDef[y][x] >= 'a' && boardDef[y][x] <= 'g')
                    createGhost(x, y, id++);
                else if (boardDef[y][x] == 's')
                    continue; // Happy Pill
                else
                    continue; // Points
            }
        }
    }
    
    private void createPacman(int x, int y, int id) {
        Waypoint centre = ((StreetSegment)board.getSegment(x, y)).getWaypointCentre();
        Pacman pac = new Pacman(Direction.East, Direction.East, centre, id);
        this.pacs.put(id, pac);
        this.sprites.put(id, pac);
        this.pacmanCount++;
    }
    
    private void createGhost(int x, int y, int id) {
        Waypoint centre = ((StreetSegment)board.getSegment(x, y)).getWaypointCentre();
        Ghost ghost = new Ghost(Direction.East, Direction.East, centre, id);
        this.ghosts.put(id, ghost);
        this.sprites.put(id, ghost);
    }
    
    public Board getBoard() {
        return this.board;
    }
    
    public Map<Integer, Sprite> getSprites() {
        return this.sprites;
    }

    @Override
    public Event peek(long currentSimtime) {
        return null;//this.nextCriticalState.getEvent();
    }

    @Override
    public void remove(Event event) {
        // TODO Auto-generated method stub
    }

    @Override
    public void offer(Event event) {
        // not used
    }

    @Override
    public void dispatchEvent(Event e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void revertHistory(List<GameState> tail) {
        if (tail.size() > 0) {
            currentState = tail.get(0);
            //fillStateQueue();
        }
    }
//    
//    public GameState poll() {
//        if (this.stateQueue.size() == 0)
//            fillStateQueue();
//        
//        return this.stateQueue.poll();
//    }
//    
//    private void fillStateQueue() {
//        this.stateQueue.clear();
//        
//        GameState lastState = currentState;
//        List<Event> events = null;
//        do {
//            lastState = new GameState(lastState);
//            this.stateQueue.offer(lastState);
//            events = generateEvents(lastState);
//        } while (events != null);
//        nextCriticalState = lastState;
//    }
    
    //private List<Event> generateEvents(GameState state) {
    //    return null;
    //}
}
