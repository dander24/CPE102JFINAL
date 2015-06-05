import javafx.util.Pair;
import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class WorldModel {
    private final int BLOB_RATE_SCALE = 10, //GOTTAGOFAST
            BLOB_ANIMATION_RATE_SCALE = 50,
            BLOB_ANIMATION_MIN = 1,
            BLOB_ANIMATION_MAX = 3,
            ORE_CORRUPT_MIN = 20000,
            ORE_CORRUPT_MAX = 30000,

    QUAKE_STEPS = 10,
            QUAKE_DURATION = 1100,
            QUAKE_ANIMATION_RATE = 100,

    VEIN_SPAWN_DELAY = 500,
            VEIN_RATE_MIN = 8000,
            VEIN_RATE_MAX = 17000;

    private ArrayList<Entity> entities;
    private OccupancyGrid background, occupancy;
    private int numRows, numCols;
    private OrderedList actionQueue;


    public WorldModel(int rows, int cols, Background _background) {
        numRows = rows;
        numCols = cols;
        background = new OccupancyGrid(numRows, numCols, _background);
        occupancy = new OccupancyGrid(numRows, numCols, null);
        entities = new ArrayList<>();
        actionQueue = new OrderedList();
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public boolean withinBounds(Point pt) {
        return (pt.getX() >= 0 && pt.getX() < numCols && pt.getY() >= 0 && pt.getY() < numRows);
    }

    public boolean isOccupied(Point pt) {
        return (withinBounds(pt) && (occupancy.getCell(pt) != null));
    }

    private Entity nearestEntity(ArrayList<Pair<Entity, Double>> EntityDistances) {
        //functions using pairs of keys[entities] and values[distances] <k,v>, a cheap but ultimately effective fix
        if (EntityDistances.size() > 0) {
            Pair<Entity, Double> pair = EntityDistances.get(0);

            for (Pair<Entity, Double> other : EntityDistances) {
                if (other.getValue() < pair.getValue()) {
                    pair = other;
                }
            }
            return pair.getKey();
        } else {
            //return null in the absence of an entity, may need to be handled or changed later
            return null;
        }
    }

    private double distanceSquared(Point pt1, Point pt2) {
        return ((pt1.getX() - pt2.getX()) * (pt1.getX() - pt2.getX()) +
                (pt1.getY() - pt2.getY()) * (pt1.getY() - pt2.getY()));
    }

    public Entity findNearest(Point pt, Class<?> cls) {
        ArrayList<Pair<Entity, Double>> ofType = new ArrayList<>();

        for (Entity e : entities) {
            if (cls.isInstance(e)) {
                ofType.add(new Pair<>(e, distanceSquared(pt, ((NonStatic) e).getPosition())));
            }
        }

        return nearestEntity(ofType);
    }

    public void addEntity(Entity entity) {
        Point pt = new Point(((NonStatic) entity).getPosition().getX(), ((NonStatic) entity).getPosition().getY());
        if (withinBounds(pt)) {
            Entity oldEntity = occupancy.getCell(pt);
            if (oldEntity != null) {
                clearPendingActions((Actor) oldEntity);
            }
            occupancy.setCell(pt, entity);
            entities.add(entity);


        }
    }

    public Point[] moveEntity(Entity entity, Point pt) {
        Point[] tiles = new Point[]{null, null};
        if (withinBounds(pt)) {
            Point oldPt = ((Actor) entity).getPosition();
            occupancy.setCell(oldPt, null);
            tiles[0] = oldPt;
            occupancy.setCell(pt, entity);
            tiles[1] = pt;
            ((Actor) entity).setPosition(pt);
        }
        return tiles;
    }

    public void worldRemoveEntity(Entity entity) {
        worldRemoveEntityAt(((Actor) entity).getPosition());
    }

    private void worldRemoveEntityAt(Point pt) {
        if (withinBounds(pt) && occupancy.getCell(pt) != null) {
            Entity entity = occupancy.getCell(pt);
            ((Actor) entity).setPosition(new Point(-1, -1));
            entities.remove(entity);
            occupancy.setCell(pt, null);
        }
    }

    public Entity getBackground(Point pt) {
        if (withinBounds(pt)) {
            return background.getCell(pt);
        }

        return null;
    }

    public void setBackground(Point pt, Entity bgnd) {
        if (withinBounds(pt)) {
            background.setCell(pt, bgnd);
        }
    }

    public Entity getTileOccupant(Point pt) {
        if (withinBounds(pt)) {
            return occupancy.getCell(pt);
        }

        return null;
    }

    public ArrayList<Entity> getEntities() {
        return entities;
    }

    //code ported from actions, would fit in a utility class but they're pretty much stray methods
    // and don't get used elsewhere (I think)

    private int sign(int x) {
        if (x < 0) {
            return -1;
        } else if (x > 0) {
            return 1;
        }

        return 0;
    }

    private boolean adjacent(Point pt1, Point pt2) {
        return (((pt1.getX() == pt2.getX()) && Math.abs(pt1.getY() - pt2.getY()) == 1) |
                ((pt1.getY() == pt2.getY()) && Math.abs(pt1.getX() - pt2.getX()) == 1));
    }

    //end code ported from actions

    public Point nextPosition(Point EntityPt, Point DestinationPt) {
        int horiz = sign(DestinationPt.getX() - EntityPt.getX());
        Point newPt = new Point(EntityPt.getX() + horiz, EntityPt.getY());

        if (horiz == 0 || isOccupied(newPt)) {
            int vert = sign(DestinationPt.getY() - EntityPt.getY());
            newPt = new Point(EntityPt.getX(), EntityPt.getY() + vert);

            if (vert == 0 || isOccupied(newPt)) {
                newPt = new Point(EntityPt.getX(), EntityPt.getY());
            }

        }
        return newPt;
    }

    public Point blobNextPosition(Point EntityPt, Point DestinationPt) {
        int horiz = sign(DestinationPt.getX() - EntityPt.getX());
        Point newPt = new Point(EntityPt.getX() + horiz, EntityPt.getY());

        if (horiz == 0 || isOccupied(newPt) && !(getTileOccupant(newPt) instanceof Ore)) {
            int vert = sign(DestinationPt.getY() - EntityPt.getY());
            newPt = new Point(EntityPt.getX(), EntityPt.getY() + vert);

            if (vert == 0 || isOccupied(newPt) && !(getTileOccupant(newPt) instanceof Ore)) {
                newPt = new Point(EntityPt.getX(), EntityPt.getY());
            }

        }
        return newPt;
    }

    public Pair<Point[], Boolean> minerToOre(Entity entity, Entity ore) {
        Point entityPoint = ((Actor) entity).getPosition();
        if (!(ore instanceof Ore)) {
            return new Pair<>(new Point[]{entityPoint}, false);
        }
        Point orePoint = ((Ore) ore).getPosition();

        if (adjacent(entityPoint, orePoint)) {
            ((Miner) entity).setResourceCount(1 + ((Miner) entity).getResourceCount());
            removeEntity((Actor) ore);
            return new Pair<>(new Point[]{orePoint}, true);
        } else {
            Point newPt = nextPosition(entityPoint, orePoint);
            return new Pair<>(moveEntity(entity, newPt), false);
        }
    }

    public Pair<Point[], Boolean> discoballToMiner(Entity entity, Entity miner) {
        Point entityPoint = ((Actor) entity).getPosition();
        if (!(miner instanceof Miner)) {
            return new Pair<>(new Point[]{entityPoint}, false);
        }
        Point minerPoint = ((Miner) miner).getPosition();
        if (adjacent(entityPoint, minerPoint)) {
            return new Pair<>(new Point[]{minerPoint}, true);
        } else {
            Point newPt = nextPosition(entityPoint, minerPoint);
            return new Pair<>(moveEntity(entity, newPt), false);
        }
    }

    public Pair<Point[], Boolean> minerToOreBlob(Entity entity, Entity oreBlob) {
        Point entityPoint = ((Actor) entity).getPosition();
        if (!(oreBlob instanceof OreBlob)) {
            return new Pair<>(new Point[]{entityPoint}, false);
        }
        Point oreBlobPoint = ((OreBlob) oreBlob).getPosition();
        if (adjacent(entityPoint, oreBlobPoint)) {
            return new Pair<>(new Point[]{oreBlobPoint}, true);
        } else {
            Point newPt = nextPosition(entityPoint, oreBlobPoint);
            return new Pair<>(moveEntity(entity, newPt), false);
        }
    }

    public Pair<Point[], Boolean> minerToSmith(Entity entity, Entity smith) {
        Point entityPoint = ((Actor) entity).getPosition();
        if (!(smith instanceof Blacksmith)) {
            return new Pair<>(new Point[]{entityPoint}, false);
        }
        Point smithPoint = ((Blacksmith) smith).getPosition();

        if (adjacent(entityPoint, smithPoint)) {
            ((Blacksmith) smith).setResourceCount(((Miner) entity).getResourceCount() +
                    ((Blacksmith) smith).getResourceCount());
            ((Miner) entity).setResourceCount(0);
            return new Pair<>(new Point[]{smithPoint}, true);
        } else {
            Point newPt = nextPosition(entityPoint, smithPoint);
            return new Pair<>(moveEntity(entity, newPt), false);
        }
    }

    public Pair<Point[], Boolean> blobToVein(Entity entity, Entity vein) {
        Point entityPoint = ((Actor) entity).getPosition();
        if (!(vein instanceof Vein)) {
            return new Pair<>(new Point[]{entityPoint}, false);
        }
        Point veinPoint = ((Vein) vein).getPosition();

        if (adjacent(entityPoint, veinPoint)) {
            removeEntity((Actor) vein);
            return new Pair<>(new Point[]{veinPoint}, true);
        } else {
            Point newPt = blobNextPosition(entityPoint, veinPoint);
            Entity oldEntity = getTileOccupant(newPt);

            if (oldEntity instanceof Ore) {
                removeEntity((Actor) oldEntity);
            }
            return new Pair<>(moveEntity(entity, newPt), false);
        }
    }

    public Point findOpenAround(Point pt, int distance) {
        for (int i = -distance; i < distance + 1; i++) {
            for (int j = -distance; j < distance + 1; j++) {
                Point newPt = new Point(pt.getX() + j, pt.getY() + i);

                if (withinBounds(newPt) && !(isOccupied(newPt))) {
                    return newPt;
                }

            }
        }

        return null;
    }

    private void removeEntity(Actor entity) {
        for (Action action : entity.getPendingActions()) {
            unscheduleAction(action);
        }
        entity.clearPendingActions();
        worldRemoveEntity(entity);
    }

    public OreBlob createBlob(String name, Point pt, int rate, Map<String, List<PImage>> imageStore) {
        Random rand = new Random();
        OreBlob b = new OreBlob(name, pt, rate, rand.nextInt((BLOB_ANIMATION_MAX * BLOB_ANIMATION_RATE_SCALE) -
                BLOB_ANIMATION_MIN) + BLOB_ANIMATION_MIN, imageStore.get("blob"));
        scheduleBlob(b, imageStore);
        return b;
    }

    public Ore createOre(String name, Point pt, Map<String, List<PImage>> imageStore) {
        Random rand = new Random();
        Ore o = new Ore(name, pt, rand.nextInt(ORE_CORRUPT_MAX - ORE_CORRUPT_MIN) + ORE_CORRUPT_MIN,
                imageStore.get("ore"));
        scheduleOre(o, imageStore);
        return o;
    }

    public Quake createQuake(Point pt, Map<String, List<PImage>> imageStore) {
        Quake q = new Quake("quake", pt, QUAKE_ANIMATION_RATE, imageStore.get("quake"));
        scheduleQuake(q);
        return q;
    }

    public Vein createVein(String name, Point pt, Map<String, List<PImage>> imageStore) {
        Random rand = new Random();
        Vein v = new Vein("vein" + name, pt, rand.nextInt(VEIN_RATE_MAX - VEIN_RATE_MIN) + VEIN_RATE_MIN, 1,
                imageStore.get("vein"));
        scheduleVein(v, imageStore);
        return v;
    }

    public Discoball createDiscoBall(String name, Point pt, Map<String, List<PImage>> imageStore) {
        Discoball d = new Discoball(name, pt, imageStore.get("discoball"), 5000, 200);
        scheduleDiscoball(d, imageStore);
        return d;
    }

    public void scheduleDiscoball(Discoball d, Map<String, List<PImage>> imageStore) {
        scheduleAnimation(d, 0);
        scheduleAction(d,
                createDiscoballAction(d, imageStore),
                System.currentTimeMillis() + d.getRate());
    }

    private Action createDiscoballAction(Discoball d, Map<String, List<PImage>> imageStore) {
        Action[] newAction = {null};
        newAction[0] = () ->
        {
            d.removePendingAction(newAction[0]);
            Point entityPoint = d.getPosition();
            Miner miner = ((Miner) findNearest(entityPoint, MinerNotFull.class));
            Pair<Point[], Boolean> newPair = discoballToMiner(d, miner);
            Miner newMiner = miner;

            if (newPair.getValue()) {
                newMiner = new DiscoMiner(
                        miner.getName(), miner.getPosition(),
                        miner.getRate()/2, miner.getResourceLimit(),
                        miner.getAnimationRate(), imageStore.get("discominer"));

                if (newMiner != miner) {
                    clearPendingActions(miner);
                    worldRemoveEntityAt(miner.getPosition());
                    addEntity(newMiner);
                    scheduleAnimation(newMiner, 0);
                }
            }
            scheduleAction(newMiner,
                    createMinerAction(newMiner, imageStore),
                    System.currentTimeMillis() + newMiner.getRate());
            scheduleAction(d,
                    createDiscoballAction(d, imageStore),
                    System.currentTimeMillis() + d.getRate());
        };
        return newAction[0];
    }

    private Action createDiscoMinerAction(Miner e, Map<String, List<PImage>> imageStore) {
        Action[] newAction = {null};
        newAction[0] = () ->
        {
            e.removePendingAction(newAction[0]);
            Point entityPoint = e.getPosition();
            OreBlob oreblob = ((OreBlob) findNearest(entityPoint, OreBlob.class));
            Pair<Point[], Boolean> newPair = minerToOreBlob(e, oreblob);
            Discoball d;

            if (newPair.getValue()) {

                d = new Discoball(
                            oreblob.getName(), oreblob.getPosition(),
                            imageStore.get("discoball"), e.getResourceLimit(),
                            e.getAnimationRate());

                        clearPendingActions(oreblob);
                        worldRemoveEntityAt(e.getPosition());
                        addEntity(d);
                        scheduleAnimation(d, 0);

                scheduleAction(d,
                        createDiscoballAction(d, imageStore),
                        System.currentTimeMillis() + d.getRate());
                }

            };

        return newAction[0];
    }


    public void updateOnTime(long time) {
        Pair<Long, Action> next = actionQueue.head();
        while (next != null && next.getKey() < time) {
            actionQueue.pop();
            next.getValue().run();
            next = actionQueue.head();

        }
    }

    public void unscheduleAction(Action action) {
        actionQueue.remove(action);
    }

    public void worldScheduleAction(long time, Action action) {
        actionQueue.insert(action, time);
    }

    public void scheduleAction(Actor e, Action action, long time) {
        e.addPendingAction(action);
        worldScheduleAction(time, action);
    }


    private void scheduleAnimation(Animated e, int repeatCount) {
        scheduleAction(e,
                createAnimationAction(e, repeatCount),
                e.getAnimationRate());
    }

    public Action createAnimationAction(Animated e, int repeatCount) {
        Action[] newAction = {null};
        newAction[0] = () ->
        {
            e.removePendingAction(newAction[0]);
            e.nextImage();

            if (repeatCount != 1) {
                scheduleAction(e, createAnimationAction(e, Math.max(0, repeatCount - 1)),
                        System.currentTimeMillis() + e.getAnimationRate());
            }

        };

        return newAction[0];
    }

    public void clearPendingActions(Actor e) {
        for (Action action : e.getPendingActions()) {
            unscheduleAction(action);
        }
        e.clearPendingActions();
    }

    private Action createMinerNotFullAction(Miner e, Map<String, List<PImage>> imageStore) {
        Action[] newAction = {null};
        newAction[0] = () ->
        {
            e.removePendingAction(newAction[0]);
            Point entityPoint = e.getPosition();
            Ore ore = ((Ore) findNearest(entityPoint, Ore.class));
            Pair<Point[], Boolean> newPair = minerToOre(e, ore);
            Miner newMiner = e;

            if (newPair.getValue()) {

                if (e.getResourceCount() > e.getResourceLimit()) {
                    newMiner = new MinerFull(
                            e.getName(), e.getPosition(),
                            e.getRate(), e.getResourceLimit(),
                            e.getAnimationRate(), e.getImages());
                    if (newMiner != e) {
                        clearPendingActions(e);
                        worldRemoveEntityAt(e.getPosition());
                        addEntity(newMiner);
                        scheduleAnimation(newMiner, 0);
                    }

                }

            }
            scheduleAction(newMiner,
                    createMinerAction(newMiner, imageStore),
                    System.currentTimeMillis() + newMiner.getRate());
        };

        return newAction[0];
    }

    private Action createMinerFullAction(Miner e, Map<String, List<PImage>> imageStore) {
        Action[] newAction = {null};
        newAction[0] = () ->
        {
            e.removePendingAction(newAction[0]);
            Point entityPoint = e.getPosition();
            Blacksmith smith = ((Blacksmith) findNearest(entityPoint, Blacksmith.class));
            Pair<Point[], Boolean> newPair = minerToSmith(e, smith);
            Miner newMiner = e;

            if (newPair.getValue()) {
                newMiner = new MinerNotFull(
                        e.getName(), e.getPosition(),
                        e.getRate(), e.getResourceLimit(),
                        e.getAnimationRate(), e.getImages());

                if (newMiner != e) {
                    clearPendingActions(e);
                    worldRemoveEntityAt(e.getPosition());
                    addEntity(newMiner);
                    scheduleAnimation(newMiner, 0);
                }
            }
            scheduleAction(newMiner,
                    createMinerAction(newMiner, imageStore),
                    System.currentTimeMillis() + newMiner.getRate());

        };


        return newAction[0];
    }

    private Action createOreBlobAction(OreBlob e, Map<String, List<PImage>> imageStore) {
        Action[] newAction = {null};
        newAction[0] = () ->
        {
            e.removePendingAction(newAction[0]);
            Point entityPoint = e.getPosition();
            Vein vein = ((Vein) findNearest(entityPoint, Vein.class));
            Pair<Point[], Boolean> newPair = blobToVein(e, vein);

            long nextTime = System.currentTimeMillis() + e.getRate();
            if (newPair.getValue()) {
                Quake quake = createQuake(newPair.getKey()[0],
                        imageStore);
                addEntity(quake);
                nextTime = System.currentTimeMillis() + e.getRate() * 2;
            }

            scheduleAction(e,
                    createOreBlobAction(e, imageStore),
                    nextTime);
        };

        return newAction[0];
    }

    private Action createVeinAction(Vein e, Map<String, List<PImage>> imageStore) {
        Action[] newAction = {null};
        newAction[0] = () ->
        {
            e.removePendingAction(newAction[0]);

            Point openPoint = findOpenAround(e.getPosition(), e.getResourceDistance());

            if (openPoint != null) {
                Ore ore = createOre("ore - " + e.getName() + " - " + Float.toString(System.currentTimeMillis()),
                        openPoint,
                        imageStore);
                addEntity(ore);
            }

            scheduleAction(e,
                    createVeinAction(e, imageStore),
                    System.currentTimeMillis() + e.getRate());
        };

        return newAction[0];
    }


    private Action createMinerAction(Miner m, Map<String, List<PImage>> imageStore) {
        if (m instanceof MinerNotFull) {
            return createMinerNotFullAction(m, imageStore);
        } else if (m instanceof MinerFull) {
            return createMinerFullAction(m, imageStore);
        } else {
            return createDiscoMinerAction(m, imageStore);
        }
    }

    private Action createEntityDeathAction(Actor e) {
        Action[] newAction = {null};
        newAction[0] = () -> {
            e.removePendingAction(newAction[0]);
            removeEntity(e);
        };

        return newAction[0];
    }

    private Action createOreTransformAction(Actor e, Map<String, List<PImage>> imageStore) {
        Action[] newAction = {null};
        newAction[0] = () -> {
            e.removePendingAction(newAction[0]);
            OreBlob b = createBlob(e.getName() + " -- blob",
                    e.getPosition(), e.getRate() / BLOB_RATE_SCALE,
                    imageStore);

            removeEntity(e);
            addEntity(b);
        };

        return newAction[0];
    }

    private void scheduleBlob(OreBlob blob, Map<String, List<PImage>> imageStore) {
        scheduleAction(blob,
                createOreBlobAction(blob, imageStore),
                System.currentTimeMillis() + blob.getRate());
        scheduleAnimation(blob, 0);
    }

    public void scheduleMiner(Miner miner, Map<String, List<PImage>> imageStore) {
        scheduleAction(miner,
                createMinerAction(miner, imageStore),
                System.currentTimeMillis() + miner.getRate());
        scheduleAnimation(miner, 0);
    }

    public void scheduleOre(Ore ore, Map<String, List<PImage>> imageStore) {
        scheduleAction(ore,
                createOreTransformAction(ore, imageStore),
                System.currentTimeMillis() + ore.getRate());
    }

    public void scheduleVein(Vein vein, Map<String, List<PImage>> imageStore) {
        scheduleAction(vein,
                createVeinAction(vein, imageStore),
                System.currentTimeMillis() + vein.getRate());
    }

    public void scheduleQuake(Quake quake) {
        scheduleAnimation(quake, QUAKE_STEPS);
        scheduleAction(quake,
                createEntityDeathAction(quake),
                System.currentTimeMillis() + QUAKE_DURATION);
    }


}


