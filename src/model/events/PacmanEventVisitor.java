package model.events;

public interface PacmanEventVisitor {
    public void visit(DirectionEvent event);
    public void visit(GhostEatenEvent event);
    public void visit(HappyPillEatenEvent event);
    public void visit(HappyPillTimeOutEvent event);
    public void visit(PacmanEatenEvent event);
    public void visit(SpriteStoppedEvent event);
}
