import processing.core.PImage;

import java.util.List;

public class Obstacle extends NonStatic {
    public Obstacle(String name, Point position, List<PImage> pImages) {
        super(name, position, pImages);
    }

    public String getSelfString() {
        return "obstacle" + getName() + Integer.toString(getPosition().getX()) + Integer.toString(getPosition().getY());
    }
}
