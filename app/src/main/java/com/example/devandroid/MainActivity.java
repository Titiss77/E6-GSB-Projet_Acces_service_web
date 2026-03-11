package com.example.devandroid; // À adapter

import android.annotation.SuppressLint;
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

    private EditText etDepartment, etName;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private RecyclerView recyclerView;

    private RequestQueue requestQueue;
    private ResultAdapter adapter;

    // L'URL de base du service web de la phase 1 [cite: 26, 27]
    private static final String BASE_URL = "https://gsb.siochaptalqper.fr/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialisation de l'interface
        etDepartment = findViewById(R.id.et_department);
        etName = findViewById(R.id.et_name);
        Button btnSearchDept = findViewById(R.id.btn_search_dept);
        Button btnSearchName = findViewById(R.id.btn_search_name);
        Button btnListDepts = findViewById(R.id.btn_list_depts);
        progressBar = findViewById(R.id.progress_bar);
        tvEmptyState = findViewById(R.id.tv_empty_state);
        recyclerView = findViewById(R.id.recycler_view);

        // Configuration du RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ResultAdapter();
        recyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);

        // Recherche par département [cite: 39, 44]
        btnSearchDept.setOnClickListener(v -> {
            String dept = etDepartment.getText().toString().trim();
            if (!dept.isEmpty()) {
                fetchData(BASE_URL + "praticiens/numdep/" + dept, false);
            } else {
                Toast.makeText(this, "Saisissez un département", Toast.LENGTH_SHORT).show();
            }
        });

        // Recherche par nom [cite: 39, 95]
        btnSearchName.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            if (!name.isEmpty()) {
                fetchData(BASE_URL + "praticiens/nom/" + name, false);
            } else {
                Toast.makeText(this, "Saisissez un nom", Toast.LENGTH_SHORT).show();
            }
        });

        // Liste des départements [cite: 71]
        btnListDepts.setOnClickListener(v -> fetchData(BASE_URL + "departements", true));
    }

    private void fetchData(String url, boolean isDepartementList) {
        // Gestion de l'UI pendant le chargement
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.GONE);

        @SuppressLint("SetTextI18n") JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
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