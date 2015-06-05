import processing.core.PImage;

import java.util.List;

public class Miner extends Animated {
    private int resourceCount, resourceLimit;

    public Miner(String name, Point position, int rate, int ResourceLimit, int AnimationRate, List<PImage> pImages) {
        super(name, position, pImages, rate, AnimationRate);
        resourceCount = 0;
        resourceLimit = ResourceLimit;

    }

    public void setResourceCount(int i) {
        resourceCount = i;
    }

    public int getResourceCount() {
        return resourceCount;
    }

    public int getResourceLimit() {
        return resourceLimit;
    }
}
