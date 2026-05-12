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

/**
 * L'activité principale de l'application.
 * <p>
 * Cette activité permet aux utilisateurs de rechercher des praticiens par nom ou par numéro de département,
 * ainsi que de lister l'ensemble des départements disponibles. Les données sont récupérées depuis
 * une API distante via la bibliothèque réseau Volley et affichées dans un {@link RecyclerView}.
 * </p>
 */
public class MainActivity extends AppCompatActivity {

    /** Champ de saisie pour la recherche (nom ou numéro de département). */
    private EditText etSearch;

    /** Barre de progression affichée lors des requêtes réseau. */
    private ProgressBar progressBar;

    /** Texte affiché lorsqu'aucun résultat n'est trouvé ou en cas d'erreur. */
    private TextView tvEmptyState;

    /** Liste déroulante pour afficher les résultats de la recherche. */
    private RecyclerView recyclerView;

    /** File d'attente pour gérer les requêtes HTTP via Volley. */
    private RequestQueue requestQueue;

    /** Adaptateur chargé de lier les données JSON aux vues du RecyclerView. */
    private ResultAdapter adapter;

    /** L'URL de base de l'API GSB. */
    private static final String BASE_URL = "https://gsb.siochaptalqper.fr/";

    /**
     * Méthode appelée lors de la création de l'activité.
     * Initialise les composants de l'interface utilisateur, configure le {@link RecyclerView},
     * initialise la file de requêtes Volley et définit les écouteurs de clics pour les boutons.
     *
     * @param savedInstanceState Si l'activité est réinitialisée après avoir été arrêtée,
     *                           ce Bundle contient les données les plus récentes fournies par
     *                           onSaveInstanceState. Sinon, il est null.
     */
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

    /**
     * Effectue une requête HTTP GET à l'URL spécifiée pour récupérer des données JSON.
     * Gère l'affichage de la barre de progression pendant le chargement et met à jour
     * l'interface utilisateur (succès, état vide ou erreur réseau) une fois la réponse reçue.
     *
     * @param url               L'URL complète de l'endpoint de l'API à interroger.
     * @param isDepartementList Un booléen indiquant la nature des données attendues :
     *                          {@code true} si l'on attend une liste de départements,
     *                          {@code false} si l'on attend une liste de praticiens.
     *                          Ce paramètre est transmis à l'adaptateur pour adapter l'affichage.
     */
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