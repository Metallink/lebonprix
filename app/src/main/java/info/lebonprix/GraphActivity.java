package info.lebonprix;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphActivity extends AppCompatActivity {

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
            hm = (HashMap<Integer, Integer>) b.get("hmGraph");

        if (hm != null)
            l = new ArrayList<Integer>(hm.keySet());

        Collections.sort(l);

        GraphView graph = (GraphView) findViewById(R.id.graph);

        DataPoint[] dp = calcul();
        /*BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 12),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6),
                new DataPoint(5, 10),
                new DataPoint(8, 7),
                new DataPoint(9, 2),
                new DataPoint(10, 3),
                new DataPoint(13, 4)
        });*/

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
        series.setValuesOnTopColor(Color.RED);
    }

    DataPoint[] calcul() {

        DataPoint[] dp = new DataPoint[hm.size()];
        for (int i = 0; i < hm.size(); i++) {
            dp[i] = new DataPoint(l.get(i), hm.get(l.get(i)));
        }
        return dp;
    }
}
