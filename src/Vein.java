import processing.core.PImage;

import java.util.List;

public class Vein extends ActorDist {
    public Vein(String name, Point position, int rate, int distance, List<PImage> pImages) {
        super(name, position, rate, distance, pImages);
    }

    public String getSelfString() {
        return "vein" + getName() + Integer.toString(getPosition().getX()) + Integer.toString(getPosition().getY())
                + Integer.toString(getRate()) + Integer.toString(getResourceDistance());
    }
}
