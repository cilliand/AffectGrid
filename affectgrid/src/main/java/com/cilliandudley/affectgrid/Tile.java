package com.cilliandudley.affectgrid;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by Cillian Dudley on 14/04/2019.
 *
 * This class is used as the tile objects that the the AffectGrid is composed of.
 */

public final class Tile {


    private final int col;
    private final int row;

    private final Paint squareColor;
    private final int squarePaint, backgroundColor;

    private RectF tileRect;

    private Boolean isTouched;


    Tile(final int col, final int row, int squarePaint, int backgroundColor) {
        this.col = col;
        this.row = row;
        this.squarePaint = squarePaint;

        this.backgroundColor = backgroundColor;

        // all squares are unselected.
        this.squareColor = new Paint();
        squareColor.setColor(backgroundColor);

        isTouched = false;

    }

    void draw(final Canvas canvas) {
        canvas.drawRect(tileRect, squareColor);
    }

    private String getColumnString() {
        return String.valueOf(col + 1);
    }

    private String getRowString() {
        // To get the actual row, add 1 since 'row' is 0 indexed.
        return String.valueOf(row + 1);
    }

    void handleTouch() {
        isTouched = true;
        squareColor.setColor(squarePaint);
    }

    void removeTouch() {
        isTouched = false;
        squareColor.setColor(backgroundColor);
    }


    boolean isTouched(final float x, final float y) {
        return tileRect.contains(x, y);
    }

    void setTileRect(final RectF tileRect) {
        this.tileRect = tileRect;
    }

    public String toString() {
        final String column = getColumnString();
        final String row = getRowString();
        return "<Tile " + column + ", " + row + ">";
    }

}