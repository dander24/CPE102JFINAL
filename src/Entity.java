import processing.core.PImage;

import java.util.List;

public class Entity {
    private String name;
    private List<PImage> images;
    private int currentImg;

    public Entity(String name, List<PImage> pImages) {
        this.name = name;
        images = pImages;
        currentImg = 0;

    }

    public String getName() {
        return name;
    }

    public List<PImage> getImages() {
        return images;
    }

    public PImage getImage() {
        return images.get(currentImg);
    }

    public void nextImage() {
        currentImg = (currentImg + 1) % images.size();
    }

    public String getSelfString() {
        return "Unknown";
    }
}
