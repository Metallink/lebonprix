package info.lebonprix;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    /* ======================= UI ELEMENTS ======================= */
    private EditText m_champs_recherche;
    private Button m_bouton;
    private TextView m_prix;

    /* ======================= ATTRIBUTS ======================= */
    private static String firstURL = "https://www.lebonprix.info/api/categorizer?q=";
    private static String m_fullURL = "https://www.lebonprix.info/api/sampling?q=";
    //private ArrayList<String> m_arrayCategory;
    private CharSequence[] chars;
    private String m_selected_category;

    /* ======================= GETTER/SETTER ======================= */
    public EditText get_champs_recherche() {
        return m_champs_recherche;
    }

    public void set_champs_recherche(EditText m_champs_recherche) {
        this.m_champs_recherche = m_champs_recherche;
    }

    public Button get_bouton() {
        return m_bouton;
    }

    public void set_bouton(Button m_bouton) {
        this.m_bouton = m_bouton;
    }

    public TextView get_prix() {
        return m_prix;
    }

    public void set_prix(TextView m_prix) {
        this.m_prix = m_prix;
    }

    /* ======================= ONCREATE() ======================= */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bindings
        m_champs_recherche = findViewById(R.id.champs_texte);
        m_bouton = findViewById(R.id.bouton_estimer);
        m_prix = findViewById(R.id.prix);

        // On click()
        m_bouton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (m_champs_recherche.length() == 0)
                    Toast.makeText(getApplicationContext(), "Veuillez entrer votre recherche", Toast.LENGTH_LONG).show();
                else {
                    sendFirstRequest(m_champs_recherche.getText().toString());
                }
            }
        });
    }

    /* ======================= ONSTART() ======================= */
    @Override
    protected void onStart() {
        super.onStart();
    }

    /* ======================= FUNCTIONS ======================= */
    private void sendFirstRequest(final String keyword) {

        final ArrayList<String> m_arrayCategory = new ArrayList<>();
        final AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);

        // building the url
        firstURL += keyword;

        //RequestQueue initialized
        RequestQueue m_request = Volley.newRequestQueue(this);
        //String Request initialized
        JsonArrayRequest m_JSONRequest = new JsonArrayRequest(Request.Method.GET, firstURL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        m_arrayCategory.add(response.getJSONArray(i).getString(0));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                // convert our ArrayList to a CharSequence[], (needed by the setItems())
                chars = m_arrayCategory.toArray(new CharSequence[m_arrayCategory.size()]);
                builderSingle.setItems(chars, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        m_selected_category = chars[which].toString();
                        sendSecondRequest(keyword.toLowerCase(), m_selected_category.toLowerCase());
                    }
                });

                // display the dialog ui
                builderSingle.show();

                // default URL
                firstURL = "https://www.lebonprix.info/api/categorizer?q=";
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i(MainActivity.class.getName(), "Error :" + error.toString());
            }
        });

        // send the request
        m_request.add(m_JSONRequest);
    }

    private void sendSecondRequest(String keyword, String category) {

        // setting the url
        m_fullURL = "https://www.lebonprix.info/api/sampling?q=" + keyword + "&c=" + category;

        final ArrayList<Integer> listPrices = new ArrayList<>();

        //RequestQueue initialized
        RequestQueue m_request = Volley.newRequestQueue(this);
        //String Request initialized
        JsonObjectRequest m_JSONRequest = new JsonObjectRequest(Request.Method.GET, m_fullURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray sample = response.getJSONArray("sample");
                    for (int i = 0; i < sample.length(); i++) {
                        listPrices.add(sample.getInt(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                int somme = calculateAverage(listPrices);
                String prix = somme + "€";
                m_prix.setText(prix);
                m_prix.setVisibility(View.VISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(MainActivity.class.getName(), "Error :" + error.toString());
            }
        });

        // send the request
        m_request.add(m_JSONRequest);
    }

    int calculateAverage(ArrayList<Integer> list) {
        double sum = 0.0;

        if (!list.isEmpty()) {
            for (Integer price : list) {
                sum += price;
            }
            return (int)Math.ceil(sum / list.size());
        }
        return (int)sum;
    }
}


// TODO Gerer les espaces et symboles dans les URLs
// TODO optimisation des request queue avec les TAGs
// TODO Fermer le clavier une fois qu'on a cliqué sur le bouton "Estimer"
// TODO Enlever la Barre du haut "leBonPrix"
// https://www.androidhive.info/2014/09/android-json-parsing-using-volley/


// https://www.lebonprix.info/api/categorizer?q=red%20dead%20redemption%202
// https://www.lebonprix.info/api/sampling?q=red%20dead%20redemption%202&c=consoles_jeux_video
