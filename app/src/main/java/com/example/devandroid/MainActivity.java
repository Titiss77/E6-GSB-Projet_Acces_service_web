package com.example.devandroid; // À adapter

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity {

    // NOUVEAU : Un seul EditText et un seul bouton de recherche
    private EditText etSearch;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private RecyclerView recyclerView;

    private RequestQueue requestQueue;
    private ResultAdapter adapter;

    private static final String BASE_URL = "https://gsb.siochaptalqper.fr/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialisation des vues
        etSearch = findViewById(R.id.et_search);
        Button btnSearch = findViewById(R.id.btn_search);
        Button btnListDepts = findViewById(R.id.btn_list_depts);
        progressBar = findViewById(R.id.progress_bar);
        tvEmptyState = findViewById(R.id.tv_empty_state);
        recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ResultAdapter();

        recyclerView.setAdapter(adapter);
        requestQueue = Volley.newRequestQueue(this);

        // NOUVEAU : Logique de la recherche intelligente
        btnSearch.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim();

            if (query.isEmpty()) {
                Toast.makeText(this, "Veuillez saisir une recherche", Toast.LENGTH_SHORT).show();
                return;
            }

            // Si la chaîne ne contient que des chiffres (ex: "29", "75")
            if (query.matches("\\d+")) {
                fetchData(BASE_URL + "praticiens/numdep/" + query, false);
            }
            // Sinon, c'est considéré comme une recherche par nom (ex: "cha")
            else {
                fetchData(BASE_URL + "praticiens/nom/" + query, false);
            }
        });

        // Afficher tous les départements reste identique
        btnListDepts.setOnClickListener(v -> {
            etSearch.setText(""); // On vide le champ de recherche pour plus de clarté
            fetchData(BASE_URL + "departements", true);
        });
    }

    private void fetchData(String url, boolean isDepartementList) {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.GONE);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    if (response.length() == 0) {
                        tvEmptyState.setText("Aucun résultat trouvé.");
                        tvEmptyState.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        adapter.setData(response, isDepartementList);
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    tvEmptyState.setText("Erreur réseau : " + error.getMessage());
                    tvEmptyState.setVisibility(View.VISIBLE);
                });

        requestQueue.add(request);
    }
}