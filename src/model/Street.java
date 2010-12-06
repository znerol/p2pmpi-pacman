package model;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Street {
    private Line2D line;
    private List<StreetSegment> streetSegments;
    
    public Street(StreetSegment streetSegment) {
        this.streetSegments = new ArrayList<StreetSegment>();
        this.streetSegments.add(streetSegment);
        this.line = new Line2D.Double(streetSegment.x, streetSegment.y, streetSegment.x, streetSegment.y);
    }
    
    public void addHorizontalStreetSegment(StreetSegment streetSegment) {
        line.setLine(line.getP1(), new Point2D.Double(streetSegment.x, streetSegment.y));
        streetSegment.horizontal = this;
        streetSegments.add(streetSegment);
    }
    
    public void addVerticalStreetSegment(StreetSegment streetSegment) {
        line.setLine(line.getP1(), new Point2D.Double(streetSegment.x, streetSegment.y));
        streetSegment.vertical = this;
        streetSegments.add(streetSegment);
        
    }
}
