package com.cilliandudley.affectgridapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.cilliandudley.affectgrid.AffectGrid;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final AffectGrid grid = findViewById(R.id.affect_grid);
        textView = findViewById(R.id.result_text_view);
        final String output = "Selected value: %d, %d \nNormalized: %.2f, %.2f";
        textView.setText(String.format(output, grid.getSelectedXValue(), grid.getSelectedYValue(), grid.getNormalizedSelectedXValue(), grid.getNormalizedSelectedYValue()));

        grid.setOnValueChangedListener(new AffectGrid.OnValueChangedListener() {
            @Override
            public void onValueChanged() {
                textView.setText(String.format(output, grid.getSelectedXValue(), grid.getSelectedYValue(), grid.getNormalizedSelectedXValue(), grid.getNormalizedSelectedYValue()));
            }
        });

        grid.setSelectedValues(Arrays.asList(new int[]{6, 0}, new int[]{7, 0}, new int[]{8, 0}));
    }
}
