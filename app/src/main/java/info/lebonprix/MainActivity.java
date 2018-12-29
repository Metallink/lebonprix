package info.lebonprix;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import RPC.GestionRequete;

public class MainActivity extends AppCompatActivity {

    /* ======================= ATTRIBUTS ======================= */
    private EditText m_champs_recherche;
    private Button m_bouton;
    private TextView m_prix;

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

        m_champs_recherche = findViewById(R.id.champs_texte);
        m_bouton = findViewById(R.id.bouton_estimer);
        m_prix = findViewById(R.id.prix);
    }

    /* ======================= ONSTART() ======================= */
    @Override
    protected void onStart() {
        super.onStart();
        //new GestionRequete(this).execute();

    }
}
