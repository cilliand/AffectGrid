package com.cilliandudley.affectgrid;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

/**
 * Created by Cillian Dudley on 18/01/2018.
 *
 * Underlying grid component of Affect Grid.
 *
 */

class Grid extends View {

    private AffectGrid affectGrid;

    private int COLS;
    private int ROWS;

    private Tile[][] mTiles;

    private int[] selectedValue;

    private float x0 = 0;
    private float y0 = 0;
    private float squareSize = 0;

    private Paint line = new Paint();

    protected boolean firstRun;

    private int sqaureColor, backgroundColor;

    private boolean touchEventsEnabled = true;

    private AffectGrid.ORIGIN origin = AffectGrid.ORIGIN.CORNER;

    private boolean isUserTouched = false;

    Grid(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    Grid(Context context) {
        super(context);
        try {
            COLS = 5;
            ROWS = 5;
            sqaureColor = getSquareColorFromTheme();
            backgroundColor = getBackgroundColorFromTheme();
        } finally {
            beginBuild();
        }
    }

    /**
     * Build Grid using XML components passed via Affect Grid.
     *
     * @param context
     * @param attrs
     */
    Grid(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.AffectGrid,
                0, 0);

        try {
            COLS = typedArray.getInt(R.styleable.AffectGrid_columns, 5);
            ROWS = typedArray.getInt(R.styleable.AffectGrid_rows, 5);

            sqaureColor = typedArray.getColor(R.styleable.AffectGrid_selectColor, getSquareColorFromTheme());
            backgroundColor = typedArray.getColor(R.styleable.AffectGrid_backgroundColor, getBackgroundColorFromTheme());
        } finally {
            beginBuild();
        }
        typedArray.recycle();
    }


    /**
     * Build Grid using XML components passed via Affect Grid.
     * Keep reference to AffectGrid parent object.
     *
     * @param context
     * @param attrs
     * @param affectGrid Parent affect grid reference
     */
    Grid(Context context, AttributeSet attrs, AffectGrid affectGrid) {
        this(context, attrs);
        this.affectGrid = affectGrid;
    }

    void beginBuild(){
        this.mTiles = new Tile[COLS][ROWS];
        buildTiles();

        setFocusable(true);

        selectedValue = new int[]{getCols() / 2, getRows() / 2};
        firstRun = true;
    }

    void setCols(int cols) {
        COLS = cols;
    }

    void setRows(int rows) {
        ROWS = rows;
    }

    int getCols() {
        return COLS;
    }

    int getRows() {
        return ROWS;
    }

    void buildTiles() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                mTiles[r][c] = new Tile(r, c, sqaureColor, backgroundColor);
            }
        }
    }


    @Override
    protected void onDraw(final Canvas canvas) {
        if (COLS != ROWS) {
            throw new RuntimeException("Columns and row count of affect grid should be equal.");
        }
        if (COLS % 2 == 0 || ROWS % 2 == 0) {
            throw new RuntimeException("Columns and rows should be an odd number to support centrality.");
        }
        final float width = getWidth();
        final float height = getHeight();
        RectF border = new RectF(0, 0, width, height);

        this.squareSize = Math.min(
                getSquareSizeWidth(width),
                getSquareSizeHeight(height)
        );

        computeOrigins(width, height);
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                final float xCoord = getXCoord(c);
                final float yCoord = getYCoord(r);

                final RectF tileRect = new RectF(
                        xCoord,               // left
                        yCoord,               // top
                        xCoord + squareSize,  // right
                        yCoord + squareSize   // bottom
                );

                mTiles[r][c].setTileRect(tileRect);
                mTiles[r][c].draw(canvas);

            }
        }
        setSelectedValue(selectedValue[0], selectedValue[1]);

        firstRun = false;
        for (int c = 0; c < COLS + 1; c++) {
            for (int r = 0; r < ROWS + 1; r++) {
                line.setColor(Color.BLACK);
                line.setStrokeWidth(1);
                canvas.drawLine(0, (squareSize * r), width, (squareSize * r), line);
                canvas.drawLine(squareSize * c, 0, squareSize * c, height, line);
            }
        }
        line.setStyle(Paint.Style.STROKE);
        canvas.drawRect(border, line);

    }

    void setSelectedValue(final float x, final float y) {
        Tile tile;
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                tile = mTiles[r][c];
                if (c == x && r == y) {
                    tile.handleTouch();
                } else {
                    tile.removeTouch();
                }
            }
        }
        invalidate();
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if (touchEventsEnabled) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                final int x = (int) event.getX();
                final int y = (int) event.getY();

                Tile tile;

                for (int r = 0; r < ROWS; r++) {
                    for (int c = 0; c < COLS; c++) {
                        tile = mTiles[r][c];
                        if (tile.isTouched(x, y)) {
                            tile.handleTouch();
                            selectedValue[0] = c;
                            selectedValue[1] = r;
                        } else {
                            tile.removeTouch();
                        }
                    }
                }
                invalidate();
            }
            if(affectGrid.getOnValueChangedListener() != null){
                affectGrid.getOnValueChangedListener().onValueChanged();
            }
        }
        return true;
    }

    void setOrigin(AffectGrid.ORIGIN origin){
        this.origin = origin;
    }

    /**
     * Returns an array with the selected row and columns indices
     *
     * If origin is center values are in the range -COLS-1/2 to COLS-1/2
     * and -ROWS-1/2 and ROWS-1/2.
     *
     * @return an array containing x and y values
     */
    int[] getSelectedValue() {
        if(origin == AffectGrid.ORIGIN.CENTER){
            int centeredX = selectedValue[0] - ((getCols() - 1) / 2);
            int centeredY = selectedValue[1] - ((getRows() - 1) / 2);
            return new int[]{centeredX, centeredY};
        }
        return selectedValue;
    }

    /**
     * @return an array containing x and y values, x and y < 0 means no value selected
     */
    boolean isValueSelected() {
        return selectedValue[0] > -1 && selectedValue[1] > -1;
    }

    float getSquareSizeWidth(final float width) {
        return width / COLS;
    }

    float getSquareSizeHeight(final float height) {
        return height / COLS;
    }

    float getXCoord(final float x) {
        return x0 + squareSize * x;
    }

    float getYCoord(final float y) {
        return y0 + squareSize * y;
    }


    void computeOrigins(final float width, final float height) {
        this.x0 = (width - (squareSize * COLS)) / 2;
        this.y0 = (height - (squareSize * ROWS)) / 2;
    }


    void disableTouchEvents() {
        this.touchEventsEnabled = false;
    }

    int getBackgroundColorFromTheme() {
        TypedValue a = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.background, a, true);
        if (a.type >= TypedValue.TYPE_FIRST_COLOR_INT && a.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            backgroundColor = a.data;
        }

        return Color.WHITE;
    }

    int getSquareColorFromTheme() {
        TypedValue a = new TypedValue();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getContext().getTheme().resolveAttribute(android.R.attr.colorAccent, a, true);
            if (a.type >= TypedValue.TYPE_FIRST_COLOR_INT && a.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                return a.data;
            }
        }

        return Color.BLACK;

    }

    /**
     * Returns an array of normalized selected values.
     *
     * Values are in the range 0 to 1 if origin is CORNER
     * If origin is CENTER values are in the range -1 to 1.
     *
     * @return float array of normalized selected values
     */
    float[] getNormalizedSelectedValue() {
        if(origin == AffectGrid.ORIGIN.CENTER){
            int max_x = (getCols() - 1) / 2;
            int min_x = -max_x;
            int max_y = (getRows() - 1) / 2;
            int min_y = -max_y;

            float centeredX = selectedValue[0] - ((getCols() - 1) / 2);
            float centeredY = selectedValue[1] - ((getRows() - 1) / 2);

            float normalizedX = 2*((centeredX - min_x) / (max_x - min_x)) - 1;
            float normalizedY = 2*((centeredY - min_y) / (max_y - min_y)) - 1;
            return new float[]{normalizedX, normalizedY};
        }
        return new float[]{getSelectedValue()[0] / (ROWS - 1f), getSelectedValue()[1] / (COLS - 1f)};
    }

    void enableTouchEvents() {
        this.touchEventsEnabled = true;
    }

    public void setSelectedValues(List<int[]> selectedValues) {

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                mTiles[r][c].removeTouch();
            }
        }

        for (int[] point : selectedValues) {
            for (int r = 0; r < ROWS; r++) {
                for (int c = 0; c < COLS; c++) {
                    if (c == point[0] && r == point[1]) {
                        mTiles[r][c].handleTouch();
                    }
                }
            }
        }
        invalidate();
    }
}