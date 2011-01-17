package model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.items.HappyPill;
import model.items.Item;
import model.items.Point;
import model.sprites.Ghost;
import model.sprites.GhostState;
import model.sprites.Pacman;
import model.sprites.PacmanState;
import model.sprites.Sprite;
import deism.core.Event;
import deism.process.DiscreteEventProcess;
import deism.stateful.AbstractStateHistory;

public class Model extends AbstractStateHistory<Long, GameState> implements DiscreteEventProcess {
    private final Board board;
    private final Map<Integer, Sprite> sprites = new HashMap<Integer, Sprite>();
    private final Map<Waypoint, Item> items = new HashMap<Waypoint, Item>();
    private int pacmanCount;
    
    public Model(char[][] boardDef, int pacmanCount) {
        this.board = new Board(boardDef);
        populateSpites(boardDef, pacmanCount);
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
                    createHappyPill(x, y);
                else
                    createPoint(x, y);
            }
        }
    }
    
    private void createPacman(int x, int y, int id) {
        Waypoint centre = ((StreetSegment)board.getSegment(x, y)).getWaypointCentre();
        PacmanState state = new PacmanState(Direction.East, Direction.East, centre, id);
        Pacman pac = new Pacman(state, this);
        this.sprites.put(id, pac);
        this.pacmanCount++;
    }
    
    private void createGhost(int x, int y, int id) {
        Waypoint centre = ((StreetSegment)board.getSegment(x, y)).getWaypointCentre();
        GhostState state = new GhostState(Direction.East, Direction.East, centre, id);
        Ghost ghost = new Ghost(state, this);
        this.sprites.put(id, ghost);
        createPoint(x, y);
    }
    
    private void createPoint(int x, int y) {
        Waypoint centre = ((StreetSegment)board.getSegment(x, y)).getWaypointCentre();
        Point point = new Point();
        this.items.put(centre, point);
    }
    
    private void createHappyPill(int x, int y) {
        Waypoint centre = ((StreetSegment)board.getSegment(x, y)).getWaypointCentre();
        HappyPill pill = new HappyPill();
        this.items.put(centre, pill);
    }
    
    public Board getBoard() {
        return this.board;
    }
    
    public Map<Integer, Sprite> getSprites() { 
        return this.sprites;
    }
    
    public void setNextDirection(Sprite sprite, Direction direction, int time) {
        // TODO Time setzen für neue Direction
    }
    
    public void rewind(int time) {
        // TODO
    }

    @Override
    public Event peek(long currentSimtime) {
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
    }
}
