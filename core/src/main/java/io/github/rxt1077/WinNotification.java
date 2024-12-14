package io.github.rxt1077;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class WinNotification implements GSprite {
    private TextureRegion frame;
    private float startTime;
    private float lastTime;
    private boolean done;
    private static int DISPLAY_SECONDS = 5;
    private int x, y, vx, vy;
    int width, height;
    private Hitmap hitmap;
    public WinNotification(int leftOrRight, float initialTime, int locationX, int locationY, int velocityX, int velocityY) {
        startTime = initialTime;
        lastTime = initialTime;
        x = locationX;
        y = locationY;
        vx = velocityX;
        vy = velocityY;
        done = false;
        if (leftOrRight == Main.LEFT) {
            frame = new TextureRegion(new Texture("left_wins.png"));
        } else {
            frame = new TextureRegion(new Texture("right_wins.png"));
        }
        width = frame.getRegionWidth();
        height = frame.getRegionHeight();
        hitmap = new Hitmap(width, height);
    }

    public void update(float stateTime) {
        float deltaTime = stateTime - lastTime;

        // stop after DISPLAY_SECONDS
        if ((stateTime - startTime) > DISPLAY_SECONDS) {
            done = true;
        }

        // bounce at edges
        if ((x > (800 - width)) || (x < 0)) {
            vx = -vx;
        }
        if ((y > (360 - height)) || (y < 0)) {
            vy = -vy;
        }

        x += (int)(deltaTime * vx);
        y += (int)(deltaTime * vy);

        lastTime = stateTime;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getType() {
        return NOTIFICATION;
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
