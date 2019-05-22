package com.cilliandudley.affectgrid;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Cillian Dudley on 14/01/2018.
 *
 * AffectGrid library component that allows user ratings on an Affect Grid
 * see http://psycnet.apa.org/record/1990-00158-001
 *
 */

public class AffectGrid extends RelativeLayout {
    /**
     * Determines if the origin should be calculated as
     * the center or right corner.
     **/
    public enum ORIGIN {
        CORNER,
        CENTER
    }

    private Grid grid;

    private int XAxisColor = Color.GREEN;
    private int YAxisColor= Color.RED;
    private boolean showAxes = true;

    private OnValueChangedListener onValueChangedListener;

    /**
     *  Inflate the affect grid layout
     */
    private void init(){
        inflate(getContext(), R.layout.affect_grid, this);
    }

    /**
     * Instantiates a new AffectGrid object, passes XML values to grid object
     * and replaces the temporary one in the view.
     *
     * @param context
     * @param attrs
     */
    public AffectGrid(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
        buildAxes(attrs);
        grid = new Grid(context, attrs, this);
        ((ViewGroup) findViewById(R.id.affect_grid_fl)).addView(grid, 0);
    }

    /**
     * Uses the XML defined attributes to visualise the axes
     *
     * @param attrs XML defined attrs
     */
    private void buildAxes(AttributeSet attrs) {
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.AffectGrid,
                0, 0);
        try {
            showAxes = typedArray.getBoolean(R.styleable.AffectGrid_highlight_axes, true);
            XAxisColor = typedArray.getColor(R.styleable.AffectGrid_x_axis_color, XAxisColor);
            YAxisColor = typedArray.getColor(R.styleable.AffectGrid_y_axis_color, YAxisColor);
        } finally {
            if(showAxes) {
                setXAxisColor(XAxisColor);
                setYAxisColor(YAxisColor);
                setShowAxes(true);
            } else {
                setShowAxes(false);
            }
        }
        typedArray.recycle();
    }

    /**
     * Set the X axis color, will also set the top and bottom axis labels to the same color.
     *
     * @param XAxisColor int color to set
     */
    public void setXAxisColor(int XAxisColor) {
        this.XAxisColor = XAxisColor;
        findViewById(R.id.x_axis).setBackgroundColor(XAxisColor);
        ((TextView)findViewById(R.id.textView_Activation)).setTextColor(XAxisColor);
        ((TextView)findViewById(R.id.textView_deactivation)).setTextColor(XAxisColor);
    }

    /**
     * Set the Y axis color, will also set the top and bottom axis labels to the same color.
     *
     * @param YAxisColor int color to set
     */
    public void setYAxisColor(int YAxisColor) {
        this.YAxisColor = YAxisColor;
        findViewById(R.id.y_axis).setBackgroundColor(YAxisColor);
        ((TextView)findViewById(R.id.unpleasant_feelings_text)).setTextColor(YAxisColor);
        ((TextView)findViewById(R.id.pleasant_feelings_text)).setTextColor(YAxisColor);
    }

    /**
     * Hide or show axes, will set axes labels colored or theme text color if not shown.
     *
     * @param showAxes
     */
    public void setShowAxes(boolean showAxes) {
        this.showAxes = showAxes;
        int visibility = showAxes ? VISIBLE : GONE;
        findViewById(R.id.x_axis).setVisibility(visibility);
        findViewById(R.id.y_axis).setVisibility(visibility);
        if(showAxes){
            ((TextView)findViewById(R.id.textView_Activation)).setTextColor(XAxisColor);
            ((TextView)findViewById(R.id.textView_deactivation)).setTextColor(XAxisColor);
            ((TextView)findViewById(R.id.unpleasant_feelings_text)).setTextColor(YAxisColor);
            ((TextView)findViewById(R.id.pleasant_feelings_text)).setTextColor(YAxisColor);
        } else {
            ((TextView)findViewById(R.id.textView_Activation)).setTextColor(getTextColorFromTheme());
            ((TextView)findViewById(R.id.textView_deactivation)).setTextColor(getTextColorFromTheme());
            ((TextView)findViewById(R.id.unpleasant_feelings_text)).setTextColor(getTextColorFromTheme());
            ((TextView)findViewById(R.id.pleasant_feelings_text)).setTextColor(getTextColorFromTheme());
        }
    }

    /**
     *  Disable user touch on the grid.
     */
    public void disableTouchEvents(){
        grid.disableTouchEvents();
    }

    /**
     *  Enabled user touch on the grid
     */
    public void enableTouchEvents(){
        grid.enableTouchEvents();
    }

    /**
     * Change the grid's origin.
     *
     * @param origin
     */
    public void setOrigin(ORIGIN origin){
        grid.setOrigin(origin);
    }


    /**
     * Get the selected x, y coordinates.
     *
     * @return integer array of x, y coordinate
     */
    public void setSelectedValue(int x, int y) {
        grid.setSelectedValue(x, y);
    }

    /**
     * Get the selected x, y coordinates.
     *
     * @return integer array of x, y coordinate
     */
    public void setSelectedValues(List<int[]> values) {
        grid.setSelectedValues(values);
    }

    /**
     * Get the selected x, y coordinates.
     * @return integer array of x, y coordinate
     */
    public int[] getSelectedValues(){
        return grid.getSelectedValue();
    }

    /**
     * Get the selected x coordinate.
     *
     * @return integer x coordinate
     */
    public int getSelectedXValue(){
        return getSelectedValues()[0];
    }

    /**
     * Get the selected y coordinate.
     *
     * @return integer y coordinate
     */
    public int getSelectedYValue(){
        return getSelectedValues()[1];
    }

    /**
     * Get the selected values as normalized values.
     *
     * @return float array of normalized x, y values
     */
    public float[] getNormalizedSelectedValues(){
        return grid.getNormalizedSelectedValue();
    }

    /**
     * Get the selected x values as a normalized value.
     *
     * @return float normalized x value
     */
    public float getNormalizedSelectedXValue(){
        return grid.getNormalizedSelectedValue()[0];
    }


    /**
     * Get the selected y value as a normalized value.
     *
     * @return float normalized y value
     */
    public float getNormalizedSelectedYValue(){
        return grid.getNormalizedSelectedValue()[1];
    }


    /**
     * Set a different OnValueChangedListener
     *
     * @param onValueChangedListener
     */
    public void setOnValueChangedListener(OnValueChangedListener onValueChangedListener){
        this.onValueChangedListener = onValueChangedListener;
    }

    public OnValueChangedListener getOnValueChangedListener() {
        return onValueChangedListener;
    }

    public int getTextColorFromTheme() {
        TypedValue a = new TypedValue();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getContext().getTheme().resolveAttribute(android.R.attr.textColor, a, true);
            if (a.type >= TypedValue.TYPE_FIRST_COLOR_INT && a.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                return a.data;
            }
        }

        return Color.BLACK;
    }

    public interface OnValueChangedListener {

        void onValueChanged();

    }
}