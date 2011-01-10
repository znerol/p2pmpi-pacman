package model;

import java.util.ArrayList;
import java.util.List;

import model.sprites.Ghost;
import model.sprites.GhostState;
import model.sprites.Pacman;
import model.sprites.PacmanState;
import model.sprites.Sprite;

public class Model {
    private final Board board;
    private final List<Sprite> sprites = new ArrayList<Sprite>();
    private int pacmanCount;
    
    public Model(char[][] boardDef, int pacmanCount) {
        this.board = new Board(boardDef);
        populateSpites(boardDef, pacmanCount);
    } 
    
    private void populateSpites(char[][] boardDef, int maxPacmanCount) {
        for (int y = 0; y < boardDef.length; y++) {
            for (int x = 0; x < boardDef[y].length; x++) {
                if (this.pacmanCount < maxPacmanCount && boardDef[y][x] >= '0' && boardDef[y][x] <= '9')
                    populatePacman(x, y);
                else if (boardDef[y][x] >= 'a' && boardDef[y][x] <= 'g')
                    populateGhost(x, y);
            }
        }
    }
    
    private void populatePacman(int x, int y) {
        Waypoint centre = ((StreetSegment)board.getSegment(x, y)).getWaypointCentre();
        PacmanState state = new PacmanState(Direction.East, Direction.East, centre);
        Pacman pac = new Pacman(state, this.pacmanCount);
        this.sprites.add(pac);
        this.pacmanCount++;
    }
    
    private void populateGhost(int x, int y) {
        Waypoint centre = ((StreetSegment)board.getSegment(x, y)).getWaypointCentre();
        GhostState state = new GhostState(Direction.East, Direction.East, centre);
        Ghost ghost = new Ghost(state);
        this.sprites.add(ghost);        
    }
    
    public Board getBoard() {
        return this.board;
    }
    
    public List<Sprite> getSprites() { 
        return this.sprites;
    }
    
    public void gotoTime(int time) {
        for (Sprite s : this.sprites) {
            s.getState(time);
        }
    }
    
    public void setNextDirection(Sprite sprite, Direction direction, int time) {
        // TODO Time setzen für neue Direction
    }
    
    public void rewind(int time) {
        // TODO
    }
}
