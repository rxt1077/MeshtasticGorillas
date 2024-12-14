package io.github.rxt1077;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Gorilla implements GSprite {
    private int x, y;
    private static final int FRAME_COLS = 5, FRAME_ROWS = 7, FRAMES = 35;
    private static int IDLE_OFFSET = 0;
    private static int IDLE_FRAMES = 20;
    private static float IDLE_INTERVAL = 1f / 20;
    private static int THROW_OFFSET = 20;
    private static int THROW_FRAMES = 10;
    private static float THROW_INTERVAL = 1f / 10;
    private static int DIE_OFFSET = 30;
    private static int DIE_FRAMES = 5;
    private static float DIE_INTERVAL = 1f /  5;
    private int frameIndex = 0;
    private float animationStartTime = 0;
    private int animationFrames = IDLE_FRAMES;
    private float animationInterval = IDLE_INTERVAL;
    private int animationIndexOffset = IDLE_OFFSET;
    TextureRegion[] frames;
    private Hitmap[] hitmaps = new Hitmap[FRAMES];
    private int type;

    public Gorilla(int locationX, int locationY, int gorillaType) {
        x = locationX;
        y = locationY;
        type = gorillaType;

        // load the sprite sheet texture

        Texture sheet = new Texture(type == GSprite.L_GORILLA ? "l_gorilla.png": "r_gorilla.png");

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
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getType() {
        return type;
    }

    public boolean isDone() {
        return false;
    }

    public Hitmap getHitmap() {
        return hitmaps[frameIndex];
    }

    public void throwAnimation(float stateTime) {
        animationStartTime = stateTime;
        animationFrames = THROW_FRAMES;
        animationInterval = THROW_INTERVAL;
        animationIndexOffset = THROW_OFFSET;
    }

    public void dieAnimation(float stateTime) {
        animationStartTime = stateTime;
        animationFrames = DIE_FRAMES;
        animationInterval = DIE_INTERVAL;
        animationIndexOffset = DIE_OFFSET;
    }

    public void update(float stateTime) {
        int framesPassed = (int)((stateTime - animationStartTime) / animationInterval);
        frameIndex = framesPassed % animationFrames + animationIndexOffset;

        // throws happen once, then we go back to idle
        if ((animationIndexOffset == THROW_OFFSET) && (framesPassed >= THROW_FRAMES)) {
            animationStartTime = stateTime;
            animationFrames = IDLE_FRAMES;
            animationInterval = IDLE_INTERVAL;
            animationIndexOffset = IDLE_OFFSET;
        // once the dying animation is done you stay down
        } else if ((animationIndexOffset == DIE_OFFSET) && (framesPassed >= DIE_FRAMES)) {
            frameIndex = DIE_OFFSET + DIE_FRAMES - 1;
        }
    }

    public TextureRegion getFrame() {
        return frames[frameIndex];
    }

    // this should never be called, but is required by the interface
    // technically animationStartTime isn't even our real start time
    public float getStartTime() {
        return animationStartTime;
    }

    public boolean getDone() {
        return false;
    }

    public void setDone(boolean isDone) {

    }
}
