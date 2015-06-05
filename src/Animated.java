import processing.core.PImage;

import java.util.List;

public class Animated extends Actor {

    private int animationRate;

    public Animated(String name, Point position, List<PImage> pImages, int rate, int animationRate) {
        super(name, position, rate, pImages);
        this.animationRate = animationRate;
    }

    public int getAnimationRate() {
        return animationRate;
    }

}
