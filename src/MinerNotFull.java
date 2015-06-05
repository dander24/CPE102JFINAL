import processing.core.PImage;

import java.util.List;

public class MinerNotFull extends Miner {
    public MinerNotFull(String name, Point position, int rate, int resourceLimit, int AnimationRate, List<PImage> pImages) {
        super(name, position, rate, resourceLimit, AnimationRate, pImages);
    }

    public String getSelfString() {
        return "miner" + getName() + Integer.toString(getPosition().getX()) + Integer.toString(getPosition().getY())
                + Integer.toString(getResourceLimit()) + Integer.toString(getRate()) + Integer.toString(getAnimationRate());
    }
}
