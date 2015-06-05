import processing.core.PImage;

import java.util.List;

public class Blacksmith extends ActorDist {
    private int resourceLimit, resourceCount;

    public Blacksmith(String name, Point position, int rate, int ResourceLimit, List<PImage> pImages) {
        super(name, position, rate, 1, pImages);
        resourceLimit = ResourceLimit;
        resourceCount = 0;
    }

    public int getResourceLimit() {
        return resourceLimit;
    }

    public int getResourceCount() {
        return resourceCount;
    }

    public void setResourceCount(int i) {
        resourceCount = i;
    }

    public String getSelfString() {
        return "blacksmith" + getName() + Integer.toString(getPosition().getX()) + Integer.toString(getPosition().getY())
                + Integer.toString(getResourceLimit()) + Integer.toString(getRate()) +
                Integer.toString(getResourceDistance());
    }
}
