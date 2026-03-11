package com.example.devandroid; // Remplacez par votre vrai nom de package

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// Imports nécessaires pour Volley et le JSON
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private EditText etDepartment, etName;
    private TextView tvResults; // Notre zone d'affichage
    private RequestQueue requestQueue; // File d'attente pour Volley

    private static final String BASE_URL = "https://gsb.siochaptalqper.fr/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialisation des vues
        etDepartment = findViewById(R.id.et_department);
        etName = findViewById(R.id.et_name);
        Button btnSearchDept = findViewById(R.id.btn_search_dept);
        Button btnSearchName = findViewById(R.id.btn_search_name);
        Button btnListDepts = findViewById(R.id.btn_list_depts);
        tvResults = findViewById(R.id.tv_results);

        // Initialisation de Volley
        requestQueue = Volley.newRequestQueue(this);

        btnSearchDept.setOnClickListener(v -> {
            String dept = etDepartment.getText().toString().trim();
            if (!dept.isEmpty()) {
                fetchDataFromApi(BASE_URL + "praticiens/numdep/" + dept);
            } else {
                Toast.makeText(MainActivity.this, "Saisissez un département", Toast.LENGTH_SHORT).show();
            }
        });

        btnSearchName.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            if (!name.isEmpty()) {
                fetchDataFromApi(BASE_URL + "praticiens/nom/" + name);
            } else {
                Toast.makeText(MainActivity.this, "Saisissez un nom", Toast.LENGTH_SHORT).show();
            }
        });

        btnListDepts.setOnClickListener(v -> fetchDataFromApi(BASE_URL + "departements"));
    }

    @SuppressLint("SetTextI18n")
    private void fetchDataFromApi(String url) {
        tvResults.setText("Chargement en cours...");

        // Création de la requête JSON (Array car l'API renvoie un tableau [...])
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        StringBuilder sb = new StringBuilder();
                        try {
                            if (response.length() == 0) {
                                tvResults.setText("Audun résultat trouvé.");
                                return;
                            }

                            // On parcourt le tableau JSON
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);

                                // Si c'est une recherche de praticien
                                if (obj.has("PRA_NOM")) {
                                    String nom = obj.getString("PRA_NOM");
                                    String prenom = obj.getString("PRA_PRENOM");
                                    String ville = obj.getString("PRA_VILLE");
                                    String specialite = obj.getString("TYP_LIBELLE");

                                    sb.append("- ").append(nom.toUpperCase()).append(" ").append(prenom).append("\n");
                                    sb.append("  Spécialité : ").append(specialite).append("\n");
                                    sb.append("  Ville : ").append(ville).append("\n\n");
                                }
                                // Si c'est la liste des départements
                                else if (obj.has("DEPARTEMENT")) {
                                    String dept = obj.getString("DEPARTEMENT");
                                    sb.append("Département : ").append(dept).append("\n");
                                }
                            }
                            // Affichage dans le TextView
                            tvResults.setText(sb.toString());

                        } catch (JSONException e) {
                            tvResults.setText("Erreur lors de la lecture des données.");
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        tvResults.setText("Erreur réseau : " + error.getMessage());
                    }
                });

        // Ajout de la requête à la file d'attente pour exécution
        requestQueue.add(request);
    }
}