package info.lebonprix;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.jjoe64.graphview.GraphView;
//import com.jjoe64.graphview.ValueDependentColor;
//import com.jjoe64.graphview.series.BarGraphSeries;
//import com.jjoe64.graphview.series.DataPoint;

public class GraphActivity extends AppCompatActivity {

    @SuppressLint("UseSparseArrays")
    private Map<Integer, Integer> hm = new HashMap<>();
    private List<Integer> l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        System.out.println("ON EST BIEN ON EST LA");
        Intent ii = getIntent();
        Bundle b = ii.getExtras();

        if (b != null)
            hm = (HashMap<Integer,Integer>)b.get("hmGraph");

        if (hm != null)
            l = new ArrayList<>(hm.keySet());

        Collections.sort(l);

        BarChart chart = findViewById(R.id.chart);
        List<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < hm.size(); i++) {

            // turn your data into Entry objects
            entries.add(new BarEntry(l.get(i), hm.get(l.get(i))));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Label");
        BarData lineData = new BarData(dataSet);
        chart.setData(lineData);
        // styling
        chart.setNoDataText("Erreur d'affichage du graphique");
        chart.setDrawBorders(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.RED);
        chart.invalidate(); // refresh

        /*GraphView graph = (GraphView) findViewById(R.id.graph);

        DataPoint[] dp = calcul();
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(dp);
        graph.addSeries(series);

        // styling
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX() * 255 / 4, (int) Math.abs(data.getY() * 255 / 6), 100);
            }
        });

        series.setSpacing(50);

        // draw values on top
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.RED);*/
    }

   /* DataPoint[] calcul() {

        DataPoint[] dp = new DataPoint[hm.size()];
        for (int i = 0; i < hm.size(); i++) {
            dp[i] = new DataPoint(l.get(i), hm.get(l.get(i)));
        }
        return dp;
    }*/
}
