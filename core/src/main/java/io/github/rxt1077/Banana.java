package io.github.rxt1077;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Banana implements GSprite {
    int x, y, xi, yi, vxi, vyi;
    private static final int FRAME_COLS = 5, FRAME_ROWS = 2, FRAMES = 10;
    Texture sheet;
    int frameIndex = 0;
    TextureRegion[] frames;
    Hitmap[] hitmaps = new Hitmap[FRAMES];
    static final int type = BANANA;
    static final int ACCELERATION = -60;
    float startTime;
    boolean done = false;

    public Banana(int positionX, int positionY, int velocityX, int velocityY, float initialTime) {
        // load the sprite sheet texture
        sheet = new Texture("banana.png");

        // create a Pixmap from it to use for our Hitmap
        if (!sheet.getTextureData().isPrepared()) {
            sheet.getTextureData().prepare();
        }
        Pixmap sheetPixmap = sheet.getTextureData().consumePixmap();

        // split the sheet up into different TextureRegions
        int width = sheet.getWidth() / FRAME_COLS;
        int height = sheet.getHeight() / FRAME_ROWS;
        TextureRegion[][] tmp = TextureRegion.split(sheet, sheet.getWidth() / FRAME_COLS, sheet.getHeight() / FRAME_ROWS);
        frames = new TextureRegion[FRAMES];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                // save the frame in the frames array
                frames[index] = tmp[i][j];

                // make a Pixmap of the region and use that to make a Hitmap of the frame
                Pixmap framePixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
                framePixmap.drawPixmap(sheetPixmap, 0, 0, j * width, i * height, width, height);
                hitmaps[index] = new Hitmap(framePixmap, true, 1);

                index++;
            }
        }

        xi = positionX;
        yi = positionY;
        vxi = velocityX;
        vyi = velocityY;
        x = xi;
        y = yi;
        startTime = initialTime;
    }

    public void update(float stateTime) {
        float deltaTime = stateTime - startTime;
        frameIndex = (int)(deltaTime / 0.1) % 10;
        x = (int)(xi + deltaTime * vxi);
        y = (int)(yi + (vyi * deltaTime + 0.5f * ACCELERATION * deltaTime * deltaTime));
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public TextureRegion getFrame() {
        return frames[frameIndex];
    }

    public Hitmap getHitmap() {
        return hitmaps[frameIndex];
    }

    public int getType() {
        return type;
    }

    public boolean getDone() {
        return done;
    }

    public void setDone(boolean isDone) {
        done = isDone;
    }

    public float getStartTime() {
        return startTime;
    }
}
