package io.github.rxt1077;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class InputDialog {
    Texture texture = new Texture("input.png");
    static final int WORLD_HEIGHT = 360;
    static final int WORLD_WIDTH = 800;
    static final int HEIGHT = 200;
    static final int WIDTH = 800;

    // display boxes are {x, y}
    static final int[] ANGLE_DISPLAY = {140, WORLD_HEIGHT - 40};
    static final int[] SPEED_DISPLAY = {535, WORLD_HEIGHT - 40};
    static final int[] TIME_DISPLAY = {485, WORLD_HEIGHT - 140};

    // touch boxes are {bottom_left_x, bottom_left_y, top_right_x, top_right_y}
    static final int[] ANGLE_INCREASE = {220, WORLD_HEIGHT - 90, 290, WORLD_HEIGHT - 20};
    static final int[] ANGLE_DECREASE = {305, WORLD_HEIGHT - 90, 375, WORLD_HEIGHT - 20};
    static final int[] SPEED_INCREASE = {625, WORLD_HEIGHT - 90, 695, WORLD_HEIGHT - 20};
    static final int[] SPEED_DECREASE = {710, WORLD_HEIGHT - 90, 780, WORLD_HEIGHT - 20};
    static final int[] THROW_IT = {255, WORLD_HEIGHT - 190, 570, WORLD_HEIGHT - 120};

    float endTime;
    int angle = 45;
    int speed = 100;
    BitmapFont angleSpeedFont = new BitmapFont(Gdx.files.internal("angle_speed_font.fnt"), Gdx.files.internal("angle_speed_font.png"), false);
    BitmapFont timeFont = new BitmapFont(Gdx.files.internal("time_font.fnt"), Gdx.files.internal("time_font.png"), false);

    static final float TOUCH_REPEAT_WAIT = 0.33f;
    float touchStartTime;

    public InputDialog(float stateTime) {
        endTime = stateTime + 60;
    }

    private boolean isTouched(Vector2 touchPos, int[] box) {
        if ((touchPos.x > box[0]) && (touchPos.x < box[2]) && (touchPos.y > box[1]) && (touchPos.y < box[3])) {
            return true;
        }
        return false;
    }

    public boolean update(SpriteBatch spriteBatch, float stateTime, FitViewport viewport) {
        // draw the dialog
        spriteBatch.draw(texture, 0, WORLD_HEIGHT - HEIGHT);

        // update text
        angleSpeedFont.draw(spriteBatch, String.format("%2d", angle), ANGLE_DISPLAY[0], ANGLE_DISPLAY[1]);
        angleSpeedFont.draw(spriteBatch, String.format("%3d", speed), SPEED_DISPLAY[0], SPEED_DISPLAY[1]);
        timeFont.draw(spriteBatch, String.format("%2d", (int)(endTime - stateTime)), TIME_DISPLAY[0], TIME_DISPLAY[1]);

        // check to see if any buttons are being pressed
        if (Gdx.input.justTouched()) {
            touchStartTime = stateTime;
        }
        // first touch acts instantly, you have to hold for TOUCH_REPEAT_WAIT to cause an action again
        if ((Gdx.input.isTouched() && ((stateTime - touchStartTime) > TOUCH_REPEAT_WAIT)) ||
            (Gdx.input.justTouched())) {
            Vector2 touchPos = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touchPos);
            if (isTouched(touchPos, ANGLE_INCREASE)) {
                if (angle < 90) {
                    angle++;
                }
            } else if (isTouched(touchPos, ANGLE_DECREASE)) {
                if (angle > 0) {
                    angle--;
                }
            } else if (isTouched(touchPos, SPEED_INCREASE)) {
                if (speed < 300) {
                    speed++;
                }
            } else if (isTouched(touchPos, SPEED_DECREASE)) {
                if (speed > 0) {
                    speed--;
                }
            } else if (isTouched(touchPos, THROW_IT)) {
                return true;
            }
        }

        // if the time has run out, start animating
        if (stateTime >= endTime) {
            return true;
        }

        return false;
    }
}
