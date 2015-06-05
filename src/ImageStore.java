import processing.core.PApplet;
import processing.core.PImage;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class ImageStore {
    PApplet parent;
    private final String DEFAULT_IMAGE_NAME = "background_default";
    private final int COLOR_MASK = 0xffffff;

    private Scanner fin;
    private Map<String, List<PImage>> images;
    private PImage defualtImg;

    public ImageStore(PApplet p) {
        parent = p;
        createDefaultImage();
    }

    private PImage createDefaultImage() {
        defualtImg = parent.loadImage("images/none.bmp");
        return defualtImg;
    }

    public Map<String, List<PImage>> loadImages(File Filename) {
        images = new HashMap<>();

        try {
            fin = new Scanner(new FileInputStream(Filename));
            while (fin.hasNextLine()) {
                String[] line = fin.nextLine().split("\\s");
                processImageLIne(images, line);
            }

            if (images.get(DEFAULT_IMAGE_NAME) == null) //I'm not sure this is actually necessary
            {
                ArrayList<PImage> a = new ArrayList();
                a.add(defualtImg);
                images.put(DEFAULT_IMAGE_NAME, a);
            }

            return images;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //this function does not account for misordered images in the original file
    //in other words, all animations must be loaded in sequential order
    private void processImageLIne(Map<String, List<PImage>> images, String[] line) {
        if (line.length >= 2) //line is not properly formatted otherwise, do nothing
        {
            String key = line[0];
            PImage nImage;
            if (line.length == 6) {
                nImage = setAlpha(parent.loadImage(line[1]), parent.color(Integer.parseInt(line[2])
                        , Integer.parseInt(line[3]), Integer.parseInt(line[4])), Integer.parseInt(line[5]));
            } else {
                nImage = parent.loadImage(line[1]);

            }

            if (images.get(key) != null) //check to see if there is already an image list to use
            {
                images.get(key).add(nImage);
            } else {
                ArrayList<PImage> a = new ArrayList();
                images.put(key, a);
                images.get(key).add(nImage);
            }

        }

    }

    public String getDEFAULT_IMAGE_NAME() {
        return DEFAULT_IMAGE_NAME;
    }

    private PImage setAlpha(PImage image, int maskColor, int alpha) {
        int alphaValue = alpha << 24;
        int nonAlpha = maskColor & COLOR_MASK;
        image.format = PApplet.ARGB;
        image.loadPixels();
        for (int i = 0; i < image.pixels.length; i++) {
            if ((image.pixels[i] & COLOR_MASK) == nonAlpha) {
                image.pixels[i] = alphaValue | nonAlpha;
            }
        }
        image.updatePixels();
        return image;
    }

}
