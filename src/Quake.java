import processing.core.PImage;

import java.util.List;

public class Quake extends Animated {

    public Quake(String name, Point position, int AnimationRate, List<PImage> pImages) {
        super(name, position, pImages, 0, AnimationRate); //inheritance is unmanageable without this having a rate
    }

}
