package RPC;

import android.os.AsyncTask;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import info.lebonprix.MainActivity;

public class GestionRequete extends AsyncTask<URL, Integer, JSONObject> {

    // ATTRIBUTS DE CLASSE
    private volatile MainActivity monActivitePrincipale;
    private URL monURL;
    private RequestQueue queue = Volley.newRequestQueue(monActivitePrincipale);
    //private StringRequest stringRequest;
    private JsonObjectRequest request;

    private Listener<String> listenerOK;
    private ErrorListener listenerErreur;

    // CONSTRUCTEUR
    public GestionRequete(MainActivity a) {
        this.monActivitePrincipale = a ;
    }


    protected void onPreExecute() {
        String url = "https://www.lebonprix.info/api/categorizer?q=";
        String recherche = monActivitePrincipale.get_champs_recherche().getText().toString();
        url.concat(recherche);

        try {
            monURL = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        //////////////////////////////////////////
        // Request a string response from the provided URL.
        //this.request = new JsonObjectRequest(Request.Method.GET, url, listenerOK, listenerErreur);

    }

    protected JSONObject doInBackground(URL... urls) {
        queue.add(request);
        return null;
    }

    protected void onPostExecute(JSONObject result) {
        // on parse
        // on envoie la liste des categories dans le Listview de MainActivity pour affichage
    }

}


// https://abhiandroid.com/programming/volley

// https://blog.webwag.com/2017/02/14/introduction-a-volley-gson/


