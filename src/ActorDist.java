import processing.core.PImage;

import java.util.List;

public class ActorDist extends Actor {
    private int resourceDistance;

    public ActorDist(String name, Point position, int rate, int distance, List<PImage> pImages) {
        super(name, position, rate, pImages);
        resourceDistance = distance;
    }

    public int getResourceDistance() {
        return resourceDistance;
    }
}
