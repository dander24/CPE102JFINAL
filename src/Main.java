import processing.core.PApplet;
import processing.core.PImage;

import ddf.minim.*;
import java.io.File;
import java.util.List;
import java.util.Map;


public class Main extends PApplet {
    private final boolean RUN_AFTER_LOAD = true;
    private final String IMAGE_LIST_FILE_NAME = "imagelist",
            DISCO_FILE = "disco.mp3",
            WORLD_FILE = "gaia.sav";
    private final int
            WORLD_WIDTH_SCALE = 2,
            WORLD_HEIGHT_SCALE = 2,
            SCREEN_WIDTH = 640,
            SCREEN_HEIGHT = 480,
            TILE_WIDTH = 32,
            TILE_HEIGHT = 32;

    private int numRows, numCols;
    private ImageStore imageStore;
    private Background defualtBackground;
    private Map<String, List<PImage>> imageMap;
    private WorldView view;
    private Minim minim;
    private AudioPlayer player;

    @Override
    public void setup() {

        //the following two lines are shamelessly borrowed from some tutorial on loading files for applets
        //I honestly don't understand exactly how they work, but they work and that's what matters
        ClassLoader classLoader = getClass().getClassLoader();
        File imageFile = new File(classLoader.getResource(IMAGE_LIST_FILE_NAME).getFile());
        File worldFile = new File(classLoader.getResource(WORLD_FILE).getFile());
        ImageStore imageStore = new ImageStore(this);
        imageMap = imageStore.loadImages(imageFile);
        numRows = (SCREEN_WIDTH / TILE_WIDTH) * WORLD_WIDTH_SCALE;
        numCols = (SCREEN_HEIGHT / TILE_HEIGHT) * WORLD_HEIGHT_SCALE;
        defualtBackground = createDefaultBackground(imageMap.get(imageStore.getDEFAULT_IMAGE_NAME()));
        WorldModel world = new WorldModel(numCols, numRows, defualtBackground);
        view = new WorldView(this, numCols, numRows, world, TILE_WIDTH, TILE_HEIGHT, SCREEN_WIDTH, SCREEN_HEIGHT);

        loadWorld(world, imageMap, worldFile);
        minim = new Minim(this);
        player = minim.loadFile(DISCO_FILE);
        size(SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    @Override
    public void draw() {
        view.updateView();
    }

    private Background createDefaultBackground(List<PImage> image) {
        return new Background("background_default", image);
    }

    private void loadWorld(WorldModel world, Map<String, List<PImage>> images, File worldFile) {
        try {
            SaveLoad worldLoader = new SaveLoad();
            worldLoader.loadWorld(world, images, worldFile, RUN_AFTER_LOAD);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void keyPressed() {
        switch (key) {
            case 'w': {
                view.shiftView(0, -1);
                break;
            }
            case 's': {
                view.shiftView(0, 1);
                break;
            }
            case 'a': {
                view.shiftView(-1, 0);
                break;
            }
            case 'd': {
                view.shiftView(1, 0);
                break;
            }
            case 'x': {
                view.updateView();
                break;
            }

        }

    }

    @Override
    public void mouseClicked()
    {
        view.discoEvent(imageMap,player);
    }

}
