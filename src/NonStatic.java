import processing.core.PImage;

import java.util.List;

public class NonStatic extends Entity {
    private Point position;

    public NonStatic(String name, Point Position, List<PImage> pImages) {
        super(name, pImages);
        this.position = Position;
    }

    public void setPosition(Point point) {
        position = point;
    }

    public Point getPosition() {
        return position;
    }
}
