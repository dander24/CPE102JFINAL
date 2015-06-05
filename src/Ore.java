import processing.core.PImage;

import java.util.List;

public class Ore extends Actor {
    public Ore(String name, Point position, int Rate, List<PImage> pImages) {
        super(name, position, Rate, pImages);
    }

    public String getSelfString() {
        return "ore" + getName() + Integer.toString(getPosition().getX()) + Integer.toString(getPosition().getY())
                + Integer.toString(getRate());
    }
}
