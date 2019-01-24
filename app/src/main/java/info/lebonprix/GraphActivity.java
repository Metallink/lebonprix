package info.lebonprix;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment;
import com.github.mikephil.charting.components.Legend.LegendOrientation;
import com.github.mikephil.charting.components.Legend.LegendVerticalAlignment;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import static com.github.mikephil.charting.animation.Easing.EaseInCubic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GraphActivity extends AppCompatActivity {

    @SuppressLint("UseSparseArrays")
    private Map<Integer, Integer> hm = new HashMap<>();
    private List<Integer> l;

    private String m_producted_searched;
    private int m_samples;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        Button m_button = findViewById(R.id.button_details);

        m_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(GraphActivity.this).create();
                alertDialog.setTitle("Pourquoi ce prix ?");
                alertDialog.setMessage("L'estimation du prix moyen est basé sur un échantillon statistiquement représentatif au niveau national et extrait en temps réel du site leboncoin.fr.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

        Intent ii = getIntent();
        Bundle b = ii.getExtras();

        if (b != null) {
            hm = (HashMap<Integer, Integer>) b.get("hmGraph");
            m_samples = (int) b.get("samples");
            m_producted_searched = (String) b.get("product");
        }

        String label = "\t" + m_producted_searched + " (échantillon de " + m_samples + " prix)";

        if (hm != null)
            l = new ArrayList<>(hm.keySet());

        Collections.sort(l);

        BarChart chart = findViewById(R.id.chart);
        List<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < hm.size(); i++) {
            // add the categories to the chart
            entries.add(new BarEntry(l.get(i), hm.get(l.get(i))));
        }

        BarDataSet dataSet = new BarDataSet(entries, label);
        BarData lineData = new BarData(dataSet);
        lineData.setBarWidth(1.1f);
        lineData.setValueTextColor(Color.rgb(232,106,48));
        lineData.setValueTextSize(12f);
        chart.setFitBars(true);
        chart.setData(lineData);
        // styling
        chart.getDescription().setEnabled(false);
        chart.setNoDataText("Erreur d'affichage du graphique");
        chart.setDrawBorders(false);
        chart.setDrawGridBackground(false);
        chart.setHighlightPerDragEnabled(false);
        chart.setScaleYEnabled(false);
        dataSet.setColor(Color.rgb(232,106,48));
        lineData.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return "" + ((int) value);
            }
        });
        // X Axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setTextSize(12f);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisLineWidth(10);
        chart.getAxisRight().setEnabled(false);
        chart.animateXY(500,1000, EaseInCubic);
        // display int instead of float
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return "" + ((int) value);
            }
        });
        // Legend
        Legend legend = chart.getLegend();
        legend.setEnabled(true);
        // position
        legend.setHorizontalAlignment(LegendHorizontalAlignment.LEFT);
        legend.setVerticalAlignment(LegendVerticalAlignment.TOP);
        legend.setOrientation(LegendOrientation.HORIZONTAL);
        legend.setForm(LegendForm.EMPTY);
        legend.setTextSize(14f);
        // Y Axis
        YAxis left = chart.getAxisLeft();
        left.setDrawGridLines(false); // no grid lines
        // display the chart
        chart.invalidate(); // refresh
    }
}
