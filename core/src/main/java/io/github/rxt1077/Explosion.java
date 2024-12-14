package io.github.rxt1077;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Explosion implements GSprite {
    int x, y;
    Texture sheet;
    TextureRegion[] frames;
    private static final int FRAME_COLS = 5, FRAME_ROWS = 2, FRAMES = 7;
    boolean done = false;
    int type = EXPLOSION;
    float startTime = 0;
    int frameIndex = 0;
    Hitmap hitmap;

    public Explosion(int locationX, int locationY, float initialTime, int radius) {
        System.out.printf("New Explosion (%d, %d)\n", locationX, locationY);
        x = locationX;
        y = locationY;
        startTime = initialTime;

        sheet = new Texture("explosion.png");
        TextureRegion[][] tmp = TextureRegion.split(sheet, sheet.getWidth() / FRAME_COLS, sheet.getHeight() / FRAME_ROWS);
        frames = new TextureRegion[FRAMES];
        frames[0] = tmp[0][0];
        frames[1] = tmp[0][1];
        frames[2] = tmp[0][2];
        frames[3] = tmp[0][3];
        frames[4] = tmp[0][4];
        frames[5] = tmp[1][0];
        frames[6] = tmp[1][1];

        // our hitmap is just a circle
        int width = frames[0].getRegionWidth();
        int height = frames[1].getRegionHeight();
        int centerX = width / 2;
        int centerY = height / 2;
        hitmap = new Hitmap(width, height);
        for (int y = centerY - radius; y < centerY + radius; y++) {
            for (int x = centerX - radius; x < centerX + radius; x++) {
                if (((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY)) <= (radius * radius)) { // equation for a circle
                    hitmap.hitmap[y][x] = 1;
                }
            }
        }
    }

    public void update(float stateTime) {
        float timeDelta = stateTime - startTime;
        // explosions run through their frames once and are done
        if (timeDelta > (FRAMES * 0.1)) {
            frameIndex = FRAMES - 1; // just in case
            done = true;
        } else {
            frameIndex = (int) (timeDelta / 0.1);
        }
    }

    public int getType() {
        return type;
    }
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean getDone() {
        return done;
    }

    public void setDone(boolean isDone) {
        done = isDone;
    }

    public TextureRegion getFrame() {
        return frames[frameIndex];
    }

    public Hitmap getHitmap() {
        return hitmap;
    }

    public float getStartTime() {
        return startTime;
    }
}
