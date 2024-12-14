package io.github.rxt1077;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MainMenu {
    Texture texture = new Texture("main_menu.png");

    // touch boxes are {bottom_left_x, bottom_left_y, top_right_x, top_right_y}
    static final int[] ONE_PLAYER_BOX = {40, 210, 370, 320};
    static final int[] TWO_PLAYERS_BOX = {430, 210, 760, 320};
    static final int[] MESHTASTIC_HOST_BOX = {40, 40, 370, 150};
    static final int[] MESHTASTIC_JOIN_BOX = {430, 40, 760, 150};
    static final int ONE_PLAYER = 0;
    static final int TWO_PLAYERS = 1;
    static final int MESHTASTIC_HOST = 2;
    static final int MESHTASTIC_JOIN = 3;
    int choice;

    private boolean isTouched(Vector2 touchPos, int[] box) {
        if ((touchPos.x > box[0]) && (touchPos.x < box[2]) && (touchPos.y > box[1]) && (touchPos.y < box[3])) {
            return true;
        }
        return false;
    }

    private void drawTouchBox(SpriteBatch spriteBatch, int[] box) {
        Pixmap pixmap = new Pixmap(box[2] - box[0], box[3] - box[1], Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.rgba8888(Color.RED));
        pixmap.fill();
        spriteBatch.draw(new Texture(pixmap), box[0], box[1]);
    }

    public boolean update(SpriteBatch spriteBatch, FitViewport viewport) {
        // draw the menu
        spriteBatch.draw(texture, 0, 0);
        /* drawTouchBox(spriteBatch, ONE_PLAYER_BOX);
        drawTouchBox(spriteBatch, TWO_PLAYERS_BOX);
        drawTouchBox(spriteBatch, MESHTASTIC_HOST_BOX);
        drawTouchBox(spriteBatch, MESHTASTIC_JOIN_BOX); */

        if (Gdx.input.justTouched()) {
            Vector2 touchPos = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touchPos);
            if (isTouched(touchPos, ONE_PLAYER_BOX)) {
                System.out.println("One player");
                choice = ONE_PLAYER;
            } else if (isTouched(touchPos, TWO_PLAYERS_BOX)) {
                System.out.println("Two players");
                choice = TWO_PLAYERS;
            } else if (isTouched(touchPos, MESHTASTIC_HOST_BOX)) {
                System.out.println("M host");
                choice = MESHTASTIC_HOST;
            } else if (isTouched(touchPos, MESHTASTIC_JOIN_BOX)) {
                System.out.println("M join");
                choice = MESHTASTIC_JOIN;
            }
            return true;
        }
        return false;
    }
}
