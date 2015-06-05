import processing.core.PImage;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class SaveLoad {
    private final int PROPERTY_KEY = 0, COLOR_MASK = 0xffffff;


    private final String BGND_KEY = "background",
            MINER_KEY = "miner",
            OBSTACLE_KEY = "obstacle",
            ORE_KEY = "ore",
            SMITH_KEY = "blacksmith",
            VEIN_KEY = "vein";

    private final int BGND_NUM_PROPERTIES = 4,
            BGND_NAME = 1,
            BGND_COL = 2,
            BGND_ROW = 3,


    MINER_NUM_PROPERTIES = 7,
            MINER_NAME = 1,
            MINER_LIMIT = 4,
            MINER_COL = 2,
            MINER_ROW = 3,
            MINER_RATE = 5,
            MINER_ANIMATION_RATE = 6,


    OBSTACLE_NUM_PROPERTIES = 4,
            OBSTACLE_NAME = 1,
            OBSTACLE_COL = 2,
            OBSTACLE_ROW = 3,


    ORE_NUM_PROPERTIES = 5,
            ORE_NAME = 1,
            ORE_COL = 2,
            ORE_ROW = 3,
            ORE_RATE = 4,


    SMITH_NUM_PROPERTIES = 7,
            SMITH_NAME = 1,
            SMITH_COL = 2,
            SMITH_ROW = 3,
            SMITH_LIMIT = 4,
            SMITH_RATE = 5,


    VEIN_NUM_PROPERTIES = 6,
            VEIN_NAME = 1,
            VEIN_RATE = 4,
            VEIN_COL = 2,
            VEIN_ROW = 3,
            VEIN_REACH = 5;

    private Scanner fin;

   /* public void saveWorld(WorldModel world, File file) {
        saveEntities(world, file);
        saveBackground(world, file);
    }

    private void saveEntities(WorldModel world, File file) {
        for (Entity e : world.getEntities()) {
            try {
                fout = new PrintWriter(file.getAbsolutePath());
                fout.println(e.getSelfString());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void saveBackground(WorldModel world, File file) {
        for (int i = 0; i < world.getNumRows(); i++) {
            for (int j = 0; j < world.getNumCols(); j++) {
                {
                    try {
                        fout = new PrintWriter(file.getAbsolutePath());
                        fout.println("background " + world.getBackground(new Point(i, j)).getName() + " "
                                + Integer.toString(i) + " " + Integer.toString(j));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }

    }*/

    public void loadWorld(WorldModel world, Map<String, List<PImage>> images, File file, Boolean runAfter) {
        boolean run = runAfter;
        try {
            fin = new Scanner(new FileInputStream(file));
            while (fin.hasNextLine()) {
                String[] properties = fin.nextLine().split("\\s");
                if (properties[PROPERTY_KEY].equals(BGND_KEY)) {
                    addBackground(world, properties, images);
                } else {
                    addEntity(world, properties, images, run);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addBackground(WorldModel world, String[] properties, Map<String, List<PImage>> images) {
        if (properties.length >= BGND_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[BGND_COL]), Integer.parseInt(properties[BGND_ROW]));
            String name = properties[BGND_NAME];
            world.setBackground(pt, new Background(name, images.get(name)));
        }
    }

    private void addEntity(WorldModel world, String[] properties, Map<String, List<PImage>> images, boolean run) {

        Entity newEntity = createFromProperties(properties, images);
        if (newEntity != null) {
            world.addEntity(newEntity);
            if (run) {
                scheduleEntity(world, newEntity, images);
            }
        }
    }

    private Entity createFromProperties(String[] properties, Map<String, List<PImage>> images) {
        String key = properties[PROPERTY_KEY];
        switch (key) {
            case (MINER_KEY): {
                return createMiner(properties, images);
            }
            case (VEIN_KEY): {
                return createVein(properties, images);
            }
            case (ORE_KEY): {
                return createOre(properties, images);
            }
            case (SMITH_KEY): {
                return createSmith(properties, images);
            }
            case (OBSTACLE_KEY): {
                return createObstacle(properties, images);
            }

        }
        return null;
    }

    private Miner createMiner(String[] properties, Map<String, List<PImage>> images) {
        if (properties.length == MINER_NUM_PROPERTIES) {
            Miner m = new MinerNotFull(properties[MINER_NAME],
                    new Point(Integer.parseInt(properties[MINER_COL]), Integer.parseInt(properties[MINER_ROW])),
                    Integer.parseInt(properties[MINER_RATE]),
                    Integer.parseInt(properties[MINER_LIMIT]),
                    Integer.parseInt(properties[MINER_ANIMATION_RATE]), images.get(MINER_KEY));
            return m;
        }
        return null;
    }

    private Vein createVein(String[] properties, Map<String, List<PImage>> images) {
        if (properties.length == VEIN_NUM_PROPERTIES) {
            Vein v = new Vein(properties[VEIN_NAME],
                    new Point(Integer.parseInt(properties[VEIN_COL]), Integer.parseInt(properties[VEIN_ROW])),
                    Integer.parseInt(properties[VEIN_RATE]),
                    Integer.parseInt(properties[VEIN_REACH]),
                    images.get(VEIN_KEY));
            return v;
        }
        return null;
    }

    private Ore createOre(String[] properties, Map<String, List<PImage>> images) {
        if (properties.length == ORE_NUM_PROPERTIES) {
            Ore o = new Ore(properties[ORE_NAME],
                    new Point(Integer.parseInt(properties[ORE_COL]), Integer.parseInt(properties[ORE_ROW])),
                    Integer.parseInt(properties[ORE_RATE]),
                    images.get(ORE_KEY));
            return o;
        }
        return null;
    }

    private Blacksmith createSmith(String[] properties, Map<String, List<PImage>> images) {
        if (properties.length == SMITH_NUM_PROPERTIES) {
            Blacksmith b = new Blacksmith(properties[SMITH_NAME],
                    new Point(Integer.parseInt(properties[SMITH_COL]), Integer.parseInt(properties[SMITH_ROW])),
                    Integer.parseInt(properties[SMITH_RATE]),
                    Integer.parseInt(properties[SMITH_LIMIT]),
                    images.get(SMITH_KEY));
            return b;
        }
        return null;
    }

    private Obstacle createObstacle(String[] properties, Map<String, List<PImage>> images) {
        if (properties.length == OBSTACLE_NUM_PROPERTIES) {
            Obstacle o = new Obstacle(properties[OBSTACLE_NAME],
                    new Point(Integer.parseInt(properties[OBSTACLE_COL]),
                            Integer.parseInt(properties[OBSTACLE_ROW])),
                    images.get(OBSTACLE_KEY));
            return o;
        }
        return null;
    }


    private void scheduleEntity(WorldModel world, Entity e, Map<String, List<PImage>> images) {
        if (e instanceof MinerNotFull) {
            world.scheduleMiner((MinerNotFull) e, images);
        }
        if (e instanceof Vein) {
            world.scheduleVein((Vein) e, images);
        }
        if (e instanceof Ore) {
            world.scheduleOre((Ore) e, images);
        }
    }
}

