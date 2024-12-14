package io.github.rxt1077;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

interface GSprite {
    public void update(float stateTime);
    public int getType();
    public int getX();
    public int getY();
    public TextureRegion getFrame();
    public Hitmap getHitmap();
    public float getStartTime();
    public void setDone(boolean isDone);
    public boolean getDone();
    static final int BANANA = 0;
    static final int EXPLOSION = 1;
    static final int L_GORILLA = 2;
    static final int R_GORILLA = 3;
    static final int NOTIFICATION = 4;
    static final int LEVEL = 5;
}
