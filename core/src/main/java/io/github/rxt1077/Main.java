package io.github.rxt1077;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.FPSLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;

enum State {
    MAIN_MENU,
    INPUT,
    ANIMATION,
    RIGHT_WINS,
    LEFT_WINS
}

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main implements ApplicationListener {
    SpriteBatch spriteBatch;
    FitViewport viewport;
    Texture background;
    Texture crosshairs;
    Level level;
    float stateTime = 0;
    State state;
    List<GSprite> sprites = new ArrayList<>();
    InputDialog inputDialog = null;
    MainMenu mainMenu;
    WinNotification winNotification = null;
    Gorilla lGorilla;
    Gorilla rGorilla;
    Hitmap spriteHitmap;
    HashSet<Integer> collisions = new HashSet<>();
    List<GSprite[]> collisionPairs = new ArrayList<GSprite[]>();
    static final int LEFT = 0;
    static final int RIGHT = 1;
    static final int EXPLOSION_RADIUS = 30;
    int turn;
    int mode;
    FPSLogger fpslogger = new FPSLogger();


    @Override
    public void create() {
        background = new Texture("background.png");
        crosshairs = new Texture("crosshairs.png");
        mainMenu = new MainMenu();
        spriteBatch = new SpriteBatch();
        spriteHitmap = new Hitmap(800, 360);
        viewport = new FitViewport(800, 360);

        state = State.MAIN_MENU;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true); // true centers the camera
    }

    // calculate the vX and vY of a thrown banana with a certain chance of hitting
    private int[] guess() {
        double angle = Math.toRadians(MathUtils.random(20, 70));
        int deltaX, deltaY;
        if (turn == LEFT) {
            deltaX = (rGorilla.getX() + 91) - (lGorilla.getX() + 80);
            deltaY = (rGorilla.getY() + 50) - (lGorilla.getY() + 80);
        } else {
            deltaX = (lGorilla.getX() + 91) - (rGorilla.getX() + 102);
            deltaY = (lGorilla.getY() + 50) - (rGorilla.getY() + 80);
        }

        double vX = deltaX / Math.sqrt(Math.abs(((2 * (deltaY - (deltaX * Math.tan(angle)))) / Banana.ACCELERATION)));
        double vY = Math.abs(vX) * Math.tan(angle);

        vX += MathUtils.random(-10, 10);
        vY += MathUtils.random(-10, 10);

        int[] retval = new int[2];
        retval[0] = (int) vX;
        retval[1] = (int) vY;
        return retval;
    }

    @Override
    public void render() {
        fpslogger.log();

        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        stateTime += Gdx.graphics.getDeltaTime();

        spriteBatch.begin();

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        spriteBatch.draw(background, 0, 0, worldWidth, worldHeight);
        //Texture testLevel = new Texture(level.pixmap);
        //spriteBatch.draw(level.getFrame(), 0, 0, worldWidth, worldHeight);
        //spriteBatch.draw(new Texture(level.hitmap.toPixmap()), 0, 0, worldWidth, worldHeight, 0, 0, 1, 1);

        switch (state) {
            case MAIN_MENU:
                // raining bananas in the background
                for (GSprite sprite : sprites) {
                    sprite.update(stateTime);
                    spriteBatch.draw(sprite.getFrame(), sprite.getX(), sprite.getY());
                    if (sprite.getY() <= 0) {
                        sprite.setDone(true);
                    }
                }
                if ((sprites.size() < 30) && (MathUtils.random() < 0.1)) {
                    sprites.add(new Banana(MathUtils.random(0, 800), 360, 0, 0, stateTime));
                }
                sprites.removeIf(GSprite::getDone);

                // check for user input
                if (mainMenu.update(spriteBatch, viewport)) {
                    mode = mainMenu.choice;
                    switch (mode) {
                        case MainMenu.ONE_PLAYER:
                        case MainMenu.TWO_PLAYERS:
                            sprites.clear();
                            level = new Level(null, viewport, stateTime);
                            sprites.add(level);
                            lGorilla = new Gorilla(
                                0,
                                level.buildingPixmaps.get(level.buildingPicks.get(0)).getHeight() - 5,
                                GSprite.L_GORILLA
                            );
                            sprites.add(lGorilla);
                            rGorilla = new Gorilla(
                                800 - 182,
                                level.buildingPixmaps.get(level.buildingPicks.get(4)).getHeight() - 5,
                                GSprite.R_GORILLA
                            );
                            sprites.add(rGorilla);
                            turn = LEFT;
                            state = State.INPUT;
                            break;
                    }
                }
                break;
            case INPUT:
                // animate and draw the level and gorillas
                spriteBatch.draw(level.getFrame(), level.getX(), level.getY(), worldWidth, worldHeight);
                level.update(stateTime);
                spriteBatch.draw(lGorilla.getFrame(), lGorilla.getX(), lGorilla.getY());
                lGorilla.update(stateTime);
                spriteBatch.draw(rGorilla.getFrame(), rGorilla.getX(), rGorilla.getY());
                rGorilla.update(stateTime);

                // draw the crosshairs
                if (turn == LEFT) {
                    spriteBatch.draw(crosshairs, rGorilla.getX() + 35, rGorilla.getY());
                } else {
                    spriteBatch.draw(crosshairs, lGorilla.getX() + 30, lGorilla.getY());
                }

                if ((mode == MainMenu.ONE_PLAYER) && (turn == RIGHT)) {
                    // generate a throw for the right gorilla
                    int[] result = guess();
                    Banana banana;
                    System.out.printf("Vx=%d Vy=%d\n", result[0], result[1]);
                    banana = new Banana(rGorilla.getX() + 102, rGorilla.getY() + 80, result[0], result[1], stateTime);
                    rGorilla.throwAnimation(stateTime);
                    sprites.add(banana);
                    state = State.ANIMATION;
                } else {
                    if (inputDialog == null) {
                        inputDialog = new InputDialog(stateTime);
                    }
                    if (inputDialog.update(spriteBatch, stateTime, viewport)) {
                        double radians = Math.toRadians(inputDialog.angle);
                        int velocityY = (int) (inputDialog.speed * Math.sin(radians));
                        Banana banana;
                        int velocityX;
                        if (turn == LEFT) {
                            velocityX = (int) (inputDialog.speed * Math.cos(radians));
                            banana = new Banana(lGorilla.getX() + 80, lGorilla.getY() + 80, velocityX, velocityY, stateTime);
                            lGorilla.throwAnimation(stateTime);
                        } else {
                            velocityX = (int) (-1 * inputDialog.speed * Math.cos(radians));
                            banana = new Banana(rGorilla.getX() + 102, rGorilla.getY() + 80, velocityX, velocityY, stateTime);
                            rGorilla.throwAnimation(stateTime);
                        }
                        sprites.add(banana);
                        state = State.ANIMATION;
                        inputDialog = null;
                    }
                }
                break;
            case ANIMATION:
                // clear the last frame's spriteHitmap
                spriteHitmap.clear();

                // go through each sprite updating, drawing, and adding its mask to spriteHitmap
                int bitMask = 1;
                //System.out.printf("There are %d sprites\n", sprites.size());
                int index = 0;
                for (GSprite gsprite : sprites) {
                    //System.out.printf("%d type=%d\n", index, gsprite.getType());
                    //index++;
                    // don't draw bananas until we're mostly done with the throw animation
                    if ((gsprite.getType() == GSprite.BANANA) &&
                        ((stateTime - gsprite.getStartTime()) < 0.4)) {
                        continue;
                    }

                    // tell the sprite to update internally
                    gsprite.update(stateTime);

                    // draw the sprite
                    spriteBatch.draw(gsprite.getFrame(), gsprite.getX(), gsprite.getY());

                    // put the sprite on the spriteHitmap
                    // note that for the spriteHitmap we add a bitmask representing this sprite's index
                    final int finalBitMask = bitMask; // this is some BS IMO
                    spriteHitmap.apply(gsprite.getHitmap(), gsprite.getX(), gsprite.getY(), (bValue, sValue) -> { return bValue + finalBitMask; });

                    switch (gsprite.getType()) {
                        case GSprite.BANANA:
                            // check to see if it's out of bounds (bananas CAN travel off screen in the UP direction)
                            if ((gsprite.getX() < 0) || (gsprite.getX() > 42 + worldWidth) ||
                                (gsprite.getY() < 0)) {
                                gsprite.setDone(true);
                            }
                            break;
                    }
                    bitMask = bitMask << 1;
                }
                //spriteBatch.draw(new Texture(spriteHitmap.toPixmap()), 0, 0, worldWidth, worldHeight, 0, 0, 1, 1);

                /* *****************************************************************************
                   ***************** Hit Detection utilizing spriteHitmap **********************
                   *****************************************************************************/

                // go through every point in the display
                collisions.clear();
                for (int y = 0; y < 360; y++) {
                    for (int x = 0; x < 800; x++) {
                        int value = spriteHitmap.hitmap[y][x];
                        // if it's not a power of two, then there is more than one bitmask at this point
                        // the first conditional (not zero) shortcuts that typical case
                        if ((value != 0) && ((value & (value - 1)) != 0)) {
                            //System.out.printf("value=%s\n", Integer.toBinaryString(value));
                            collisions.add(value);
                        }
                    }
                }

                // determine all the pairs of impacts for a collision
                collisionPairs.clear();
                for (int collision: collisions) {
                    int index1 = 0;
                    for (int tmp = collision; tmp > 0; tmp = tmp >> 1) {
                        if ((tmp & 1) != 0) {
                            int index2 = 0;
                            for (int tmp2 = collision; tmp2 > 0; tmp2 = tmp2 >> 1) {
                                if (((tmp2 & 1) != 0) && (index1 != index2)) {
                                    GSprite[] pair = new GSprite[2];
                                    pair[0] = sprites.get(index1);
                                    pair[1] = sprites.get(index2);
                                    collisionPairs.add(pair);
                                }
                                index2++;
                            }
                        }
                        index1++;
                    }
                }

                // game logic for collisions
                for (GSprite[] pair : collisionPairs) {
                    // pair[0] is the thing that got hit
                    // pair[1] is the thing that did the hitting
                    switch (pair[0].getType()) {
                        case GSprite.BANANA:
                            // bananas explode no matter what they hit
                            System.out.println("A banana exploded!");
                            pair[0].setDone(true);
                            System.out.printf("(%d, %d)\n", pair[0].getX(), pair[0].getY());
                            sprites.add(new Explosion(pair[0].getX() - 75, pair[0].getY() - 75, stateTime, EXPLOSION_RADIUS));
                            break;
                        case GSprite.L_GORILLA:
                        case GSprite.R_GORILLA:
                            // gorillas are killed by bananas and explosions
                            switch (pair[1].getType()) {
                                case GSprite.BANANA:
                                case GSprite.EXPLOSION:
                                    if (pair[0].getType() == GSprite.L_GORILLA) {
                                        lGorilla.dieAnimation(stateTime);
                                        state = State.RIGHT_WINS;
                                    } else {
                                        rGorilla.dieAnimation(stateTime);
                                        state = State.LEFT_WINS;
                                    }
                                    break;
                            }
                            break;
                        case GSprite.LEVEL:
                            switch(pair[1].getType()) {
                                case GSprite.EXPLOSION:
                                    System.out.println("The level was hit by an explosion!");
                                    level.explode(pair[1]);
                                    break;
                            }
                    }
                }

                // delete sprites that are done
                sprites.removeIf(GSprite::getDone);

                // once we're out of bananas and explosions, head back to user input
                if (state == State.ANIMATION) {
                    boolean doneAnimating = true;
                    for (GSprite sprite: sprites) {
                        if ((sprite.getType() == GSprite.BANANA) || (sprite.getType() == GSprite.EXPLOSION)) {
                            doneAnimating = false;
                            break;
                        }
                    }
                    if (doneAnimating) {
                        state = State.INPUT;
                        turn = turn == LEFT ? RIGHT : LEFT;
                    }
                }
                break;
            case LEFT_WINS:
            case RIGHT_WINS:
                if (winNotification == null) {
                    if (state == State.LEFT_WINS) {
                        winNotification = new WinNotification(LEFT, stateTime, 0, 0, 200, 200);
                    } else {
                        winNotification = new WinNotification(RIGHT, stateTime, 511, 0, -200, 200);
                    }
                    sprites.add(winNotification);
                }
                for (GSprite sprite: sprites) {
                    sprite.update(stateTime);
                    spriteBatch.draw(sprite.getFrame(), sprite.getX(), sprite.getY());
                }
                sprites.removeIf(GSprite::getDone);
                if (winNotification.getDone()) {
                    winNotification = null;
                    sprites.clear();
                    state = State.MAIN_MENU;
                }
                break;
        }

        spriteBatch.end();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
    }
}
