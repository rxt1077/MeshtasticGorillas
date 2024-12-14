package io.github.rxt1077;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;

public class Level implements GSprite {
    Pixmap pixmap;
    private TextureRegion frame;
    Hitmap hitmap;
    static final String[] buildingFiles = {
        "building_01.png", "building_02.png", "building_03.png", "building_04.png",
        "building_05.png", "building_06.png", "building_07.png", "building_08.png",
        "building_09.png", "building_10.png", "building_11.png", "building_12.png",
    };
    ArrayList<Pixmap> buildingPixmaps = new ArrayList<>();
    ArrayList<Integer> buildingPicks = new ArrayList<>();
    static final int BUILDINGS_IN_LEVEL = 5;
    private float startTime;
    private boolean done = false;

    public Level(ArrayList<Integer> buildings, FitViewport viewport, float initialTime) {
        startTime = initialTime;

        // load Textures and create Pixmaps
        for (String fileName : buildingFiles) {
            Texture buildingTexture = new Texture(fileName);
            buildingTexture.getTextureData().prepare();
            buildingPixmaps.add(buildingTexture.getTextureData().consumePixmap());
        }

        // create the Pixmap
        pixmap = new Pixmap(800, 360, Pixmap.Format.RGBA8888);

        // create the buildings array if we weren't passed one
        if (buildings == null) {
            // don't put the same building in the map multiple times
            int pick;
            for (int i = 0; i < BUILDINGS_IN_LEVEL; i++ ) {
                do {
                    pick = MathUtils.random(0, buildingPixmaps.size() - 1);
                } while (buildingPicks.contains(pick));
                buildingPicks.add(pick);
            }
        } else {
            buildingPicks = buildings;
        }

        drawPixmap();
        hitmap = new Hitmap(pixmap, true, 1);
        frame = new TextureRegion(new Texture(pixmap));

        // we want to be able to paint CLEAR pixels overtop of this pixmap
        pixmap.setBlending(Pixmap.Blending.None);
    }

    // draws the buildings on the pixmap
    void drawPixmap() {
        // draw buildings with a little overlap, even buildings first then odd buildings on top
        int x = -10;
        for (int i = 0; i < BUILDINGS_IN_LEVEL; i += 2) {
            int building = buildingPicks.get(i);

            pixmap.drawPixmap(buildingPixmaps.get(building), x, 360 - buildingPixmaps.get(building).getHeight());
            x += 160 * 2;
        }
        x = 150;
        for (int i = 1; i < BUILDINGS_IN_LEVEL; i += 2) {
            int building = buildingPicks.get(i);

            pixmap.drawPixmap(buildingPixmaps.get(building), x, 360 - buildingPixmaps.get(building).getHeight());
            x += 160 * 2;
        }
    }

    // clears the explosion hitmap from the level's hitmap
    // draws a hole (in the shape of the explosion hitmap) in the level's pixmap
    // updates frame
    void explode(GSprite explosion) {
        System.out.println("Inside explosion");
        for (int y = 0; y < explosion.getHitmap().height;  y++) {
            for (int x = 0; x < explosion.getHitmap().width; x++) {
                int levelX = x + explosion.getX();
                int levelY = y + explosion.getY();
                if ((explosion.getHitmap().hitmap[y][x] != 0) &&
                    (levelX >= 0) && (levelX < 800) &&
                    (levelY >= 0) && (levelY < 360)) {
                    hitmap.hitmap[levelY][levelX] = 0;
                    pixmap.drawPixel(levelX, 360 - levelY, Color.rgba8888(Color.CLEAR));
                }
            }
        }
        frame = new TextureRegion(new Texture(pixmap));
    }

    public void update(float stateTime) {
    }

    public int getType() {
        return LEVEL;
    }

    public int getX() {
        return 0;
    }

    public int getY() {
        return 0;
    }

    public TextureRegion getFrame() {
        return frame;
    }

    public Hitmap getHitmap() {
        return hitmap;
    }

    public float getStartTime() {
        return startTime;
    }

    public boolean getDone() {
        return done;
    }

    public void setDone(boolean isDone) {
        done = isDone;
    }
}
