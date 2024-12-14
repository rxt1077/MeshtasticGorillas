package io.github.rxt1077;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;

interface ApplyFunction {
    int run(int bigHitmapValue, int smallHitmapValue);
}

/** Hitmap tracks which parts of a Pixmap should trigger hit detection
 */
public class Hitmap {
    int width;
    int height;
    int[][] hitmap;

    public Hitmap(int mapWidth, int mapHeight) {
        width = mapWidth;
        height = mapHeight;
        hitmap = new int[mapHeight][mapWidth];
    }
    public Hitmap(Pixmap pixmap, boolean flipY, int value) {
        width = pixmap.getWidth();
        height = pixmap.getHeight();
        hitmap = new int[height][width];

        // when you make Pixmaps from Textures they can be upside down, this corrects for that
        if (flipY) {
            Pixmap flipped = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), Pixmap.Format.RGBA8888);
            for (int y = 0; y < pixmap.getHeight(); y++) {
                flipped.drawPixmap(pixmap, 0, pixmap.getHeight() - y - 1, 0, y, pixmap.getWidth(), 1);
            }
            pixmap = flipped;
        }

        // go through each pixel in the pixmap
        for (int y = 0; y < pixmap.getHeight(); y++) {
            for (int x = 0; x < pixmap.getWidth(); x++) {
                int pixel = pixmap.getPixel(x, y);
                int alpha = pixel & 0x000000FF;
                if (alpha > 0x80) { // if the pixel is >50% opaque
                    hitmap[y][x] = value;
                }
            }
        }
    }

    // for every non-zero location in the smallHitmap this allows you to apply a function to the
    // large hitmap
    void apply(Hitmap smallHitmap, int locationX, int locationY, ApplyFunction apply) {
        // only check the visible areas
        int yStart = 0;
        int xStart = 0;
        if (locationY < 0) {
            yStart = -locationY;
        }
        if (locationX < 0) {
            xStart = -locationX;
        }
        int yEnd = smallHitmap.height;
        int xEnd = smallHitmap.width;
        if ((locationY + yEnd) >= height) {
            yEnd -= (locationY + yEnd) - height;
        }
        if ((locationX + xEnd) >= width) {
            xEnd -= (locationX + xEnd) - width;
        }

        // go through each pixel in the pixmap and apply the function IF the smallHitmap pixel is
        // set
        for (int y = yStart; y < yEnd; y++) {
            for (int x = xStart; x < xEnd; x++) {
                int sValue = smallHitmap.hitmap[y][x];
                if (sValue != 0) {
                    int bIndexY = locationY + y;
                    int bIndexX = locationX + x;
                    int bValue = hitmap[bIndexY][bIndexX];
                    hitmap[bIndexY][bIndexX] = apply.run(bValue, sValue);
                }
            }
        }
    }
    Pixmap toPixmap() {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                switch(hitmap[y][x]) {
                    case 0:
                        pixmap.drawPixel(x, y, Color.rgba8888(Color.CLEAR));
                        break;
                    default:
                        pixmap.drawPixel(x, y, Color.rgba8888(Color.RED));
                        break;
                }
            }
        }
        return pixmap;
    }

    void clear() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                hitmap[y][x] = 0;
            }
        }
    }
}
