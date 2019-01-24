package info.lebonprix;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import java.util.HashMap;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    /* ======================= UI ELEMENTS ======================= */
    private EditText m_champs_recherche;
    private TextView m_prix;
    private Button m_button_details;

    /* ======================= ATTRIBUTS ======================= */
    private static String firstURL = "https://www.lebonprix.info/api/categorizer?q=";

    private String m_selected_category;
    private CharSequence[] chars;
    private SparseIntArray m_hash_map = new SparseIntArray();
    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, Integer> m_hm = new HashMap<>();
    private RequestQueue m_request = null;
    int m_somme = 0;
    int m_sample = 0; // samples

    // avoid multiple clicks
    private long mLastClickTime = 0;

    /* ======================= ONCREATE() ======================= */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bindings
        m_champs_recherche = findViewById(R.id.champs_texte);
        Button m_button_search = findViewById(R.id.bouton_estimer);
        m_prix = findViewById(R.id.prix);
        m_button_details = findViewById(R.id.bouton_details);

        // On click() search button
        m_button_search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (m_champs_recherche.length() == 0)
                    Toast.makeText(getApplicationContext(), "Veuillez entrer votre recherche", Toast.LENGTH_LONG).show();
                else if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    // second clicked (less than 1s after the first one)
                    System.out.println("trop vite");
                } else { // one click
                    mLastClickTime = SystemClock.elapsedRealtime();
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); // close the keyboard
                    assert inputManager != null;
                    inputManager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    // sending the http request
                    sendFirstRequest(m_champs_recherche.getText().toString());
                }
            }
        });

        // On click() details button
        m_button_details.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    // preventing multiple clicks
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                // if the price is null, we won't go into the graphActivity
                if (m_somme == 0) {
                    Toast.makeText(getApplicationContext(), "Pas de résultat à afficher", Toast.LENGTH_LONG).show();
                } else {
                    // go to the second activity
                    Intent detailsIntent = new Intent(MainActivity.this, GraphActivity.class);
                    detailsIntent.putExtra("hmGraph", m_hm);
                    detailsIntent.putExtra("samples", m_sample);
                    detailsIntent.putExtra("product", m_champs_recherche.getText().toString());
                    startActivity(detailsIntent);
                }
            }
        });
    }

    /**
     * This method is used to prepare the URL, sending the http request using the Volley libraby
     * and in a second time, treating the http response by collecting the different categories sent
     * by the server. It also displays the categories to the users.
     *
     * @param keyword String containing the name of the product that the user is searching
     */
    private void sendFirstRequest(final String keyword) {

        final ArrayList<String> m_arrayCategory = new ArrayList<>();
        m_arrayCategory.add("Toutes catégories");

        final AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
        builderSingle.setTitle("Choisissez une catégorie");

        // building the url
        String keyword_parsed = keyword;
        keyword_parsed = keyword_parsed.replaceAll(" ", "%20");
        firstURL += keyword_parsed;

        if (m_request == null)
            m_request = Volley.newRequestQueue(this);

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
                chars = m_arrayCategory.toArray(new CharSequence[0]);
                builderSingle.setItems(chars, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which != 0) {
                            m_selected_category = chars[which].toString();
                            sendSecondRequest(keyword.toLowerCase(), m_selected_category.toLowerCase());
                        } else {
                            m_selected_category = "";
                            sendSecondRequest(keyword.toLowerCase(), m_selected_category);
                        }
                    }
                });

                // Create the alert dialog
                AlertDialog dialog = builderSingle.create();
                // Get the alert dialog ListView instance
                ListView listView = dialog.getListView();
                // Set the divider color of alert dialog list view
                listView.setDivider(new ColorDrawable(Color.parseColor("#E56C2A")));
                // Set the divider height of alert dialog list view
                listView.setDividerHeight(1);
                // Finally, display the alert dialog
                dialog.show();

                // setting back the default URL
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

    /**
     * This method is taking care of the second request which is sending the http request to the server using
     * the Volley library.
     * <p>
     * First, it is preparing, building, parsing the URL and sending the http request.
     * Secondly, it is listening for the http response containing the values. It puts the data
     * into an hash map and calculating the average price of the searched product.
     *
     * @param keyword  String containing the name of the product that the user is searching
     * @param category String containing the category that has been selected by the user
     */
    private void sendSecondRequest(String keyword, String category) {
        // empty hash maps;
        m_hash_map.clear();

        // setting the URL
        Uri.Builder ub = new Builder();
        ub.scheme("https")
                .authority("www.lebonprix.info")
                .path("api/sampling")
                .appendQueryParameter("q", keyword)
                //.appendQueryParameter("c", category)
                .build();

        // adding the category using the method parseCategory()
        String fullURL = ub.toString().concat("&c=" + parseCategory(category));

        //final Map<Integer,Integer> hm = new HashMap<>();
        // like HashMap but better as it is more efficient
        //final SparseIntArray hm = new SparseIntArray();

        // temporary list that will contain the prices (the real list is the hash map)
        final ArrayList<Integer> listPrices = new ArrayList<>();

        //RequestQueue initialized
        final RequestQueue m_request = Volley.newRequestQueue(this);
        //String Request initialized
        final JsonObjectRequest m_JSONRequest = new JsonObjectRequest(Request.Method.GET, fullURL, null, new Response.Listener<JSONObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray sample = response.getJSONArray("sample");
                    for (int i = 0; i < sample.length(); i++) {
                        // adding the price to the list
                        listPrices.add(sample.getInt(i));
                        // if price doesn't exist
                        if (m_hash_map.indexOfKey(sample.getInt(i)) < 0) {
                            // we add it and init its number of occurences  to 1
                            m_hash_map.append(sample.getInt(i), 1);
                        } else {
                            // the price already exist in the hash map so we simply inc its number of occurences
                            m_hash_map.append(sample.getInt(i), m_hash_map.get(sample.getInt(i)) + 1);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // empty the previous hm
                m_hm.clear();

                for (int j = 0; j < m_hash_map.size(); j++) {
                    m_hm.put(m_hash_map.keyAt(j), m_hash_map.get(m_hash_map.keyAt(j)));
                }

                // displaying the price
                m_somme = calculateAverage(listPrices);

                if (m_somme == 0) {
                    m_prix.setText("Inconnu");
                    m_prix.setVisibility(View.VISIBLE);
                } else {
                    String prix = m_somme + "€";
                    m_prix.setText(prix);
                    m_prix.setVisibility(View.VISIBLE);
                }

                // display the details button
                m_button_details.setVisibility(View.VISIBLE);
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

    /**
     * This method is an utility method that is being used to calculated the average price
     * given a list containing prices of the searched product.
     *
     * @param uneListe an array containing different prices for the product
     * @return the average price of the product
     */
    int calculateAverage(ArrayList<Integer> uneListe) {
        double sum = 0.0;

        if (!uneListe.isEmpty()) {
            for (Integer price : uneListe) {
                sum += price;
            }
            m_sample = uneListe.size();
            return (int) Math.ceil(sum / uneListe.size());
        }
        return (int) sum;
    }

    /**
     * This method is an utility method that is used to prepare the category for the second request's
     * URL. The URL must be cleaned from spaces, different accents...
     *
     * @param cat the category that we want to parse
     * @return the category parsed (withtout accents and spaces)
     */
    String parseCategory(String cat) {

        String s = cat.trim();
        s = s.replaceAll("[éèêë]", "e");
        s = s.replaceAll("[îï]", "i");
        s = s.replaceAll("[ôö]", "o");
        s = s.replaceAll("'", "_");
        s = s.replaceAll(" & ", "_");
        s = s.replaceAll(" - ", "_");
        s = s.replaceAll(" ", "_");

        return s;
    }
}