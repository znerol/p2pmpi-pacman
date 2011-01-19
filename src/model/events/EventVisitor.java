package model.events;

public interface EventVisitor {
    public void visit(DirectionEvent event);
    public void visit(CollisionEvent event);
    public void visit(ChangeViewEvent event);
    public void visit(EnterJunctionEvent event);
}
