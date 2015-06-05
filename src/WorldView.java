import ddf.minim.AudioPlayer;
import javafx.scene.media.MediaPlayer;
import processing.core.PApplet;
import processing.core.PImage;

import java.util.List;
import java.util.Map;

public class WorldView {
    private int viewRows, viewCols, tileWidth, tileHeight, numRows, numCols, drawWidth, drawHeight, drawX, drawY;
    private WorldModel world;
    private PApplet parent;
    private PImage searched, path;
    private boolean discoEventLive = false;
    private int loops = 0;

    public WorldView(PApplet parentApp, int viewCol, int viewRow, WorldModel worldMod, int tileWid, int tileHei,
                     int screenWidth, int screenHeight) {
        world = worldMod;
        tileWidth = tileWid;
        tileHeight = tileHei;
        viewCols = viewCol;
        viewRows = viewRow;
        numRows = world.getNumRows();
        numCols = world.getNumCols();
        drawWidth = screenWidth / tileWidth;
        drawHeight = screenHeight / tileHeight;
        drawX = 0;
        drawY = 0;
        parent = parentApp;


    }

    public void updateView() {
        drawBackground();
        drawEntities();
        world.updateOnTime(System.currentTimeMillis());

        if (discoEventLive)
        {

            if(loops == 0){
                int r = (int)(255 * Math.random());
                int g = (int)(255 * Math.random());
                int b = (int)(255 * Math.random());
                parent.noTint();
                parent.tint(r,g,b);
            }
            loops++;

            if (loops > 30)
            {
                loops = 0;
            }
        }

        int row = (parent.mouseY / tileWidth) + drawY;
        int col = (parent.mouseX / tileHeight) + drawX;

    }


    private Point viewportToWorld(Point pt) {
        return new Point(pt.getX() + drawX, pt.getY() + drawY);
    }

    private void drawEntities() {
        for (int i = 0; i < drawHeight; i++) {
            for (int j = 0; j < drawWidth; j++) {
                Entity next = world.getTileOccupant(viewportToWorld(new Point(j, i)));
                if (next != null) {
                    parent.image(next.getImage(), j * tileWidth, i * tileHeight);
                }
            }

        }
    }

    private void drawBackground() {
        for (int i = 0; i < drawHeight; i++) {
            for (int j = 0; j < drawWidth; j++) {
                Entity next = world.getBackground(viewportToWorld(new Point(j, i)));
                if (next != null) {
                    parent.image(next.getImage(), j * tileWidth, i * tileHeight);
                }
            }

        }
    }


    public void shiftView(int deltaX, int deltaY) {
        drawX = clamp(drawX + deltaX, 0, numCols - drawWidth);
        drawY = clamp(drawY + deltaY, 0, numRows - drawHeight);
    }

    private int clamp(int i1, int i2, int i3) {
        return Math.min(i3, Math.max(i1, i2));
    }

    public void discoEvent(Map<String, List<PImage>> imageStore, AudioPlayer player) {
        Background dancefloor = new Background("dancefloor", imageStore.get("dancefloor"));
        if (!discoEventLive) {
            player.play();
            for (int i = -2; i < 3; i++) {
                for (int j = -2; j < 3; j++) {
                    if (world.withinBounds(viewportToWorld(new Point(parent.mouseX / 32, parent.mouseY / 32)))) {
                        Point p = new Point(parent.mouseX / 32 + j, parent.mouseY / 32 + i);
                        world.setBackground(viewportToWorld(p), dancefloor);
                        p = viewportToWorld(p);
                        if (world.isOccupied(p))
                        {
                            if(world.getTileOccupant(p) instanceof Miner)
                            {
                                Miner m = (Miner)world.getTileOccupant(p);
                                DiscoMiner d = new DiscoMiner(m.getName(),p,m.getRate(),m.getResourceLimit(),m.getAnimationRate(),imageStore.get("discominer"));
                                world.worldRemoveEntity(m);
                                world.addEntity(d);
                                world.scheduleMiner(d,imageStore);
                            }
                        }
                    }

                }
            }
            Discoball d = world.createDiscoBall("MAKEITMAKEMONEY", viewportToWorld(new Point(parent.mouseX / 32, parent.mouseY / 32)), imageStore);
            world.addEntity(d);
            world.scheduleDiscoball(d, imageStore);
            discoEventLive = true;
        }

    }

}
