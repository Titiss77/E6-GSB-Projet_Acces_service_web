package com.example.devandroid; // À adapter

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Adaptateur pour le {@link RecyclerView} permettant de lier les données JSON à l'interface utilisateur.
 * <p>
 * Cet adaptateur est polyvalent : il gère l'affichage de deux types de listes différentes
 * (les départements et les praticiens) en utilisant la même vue de base ({@code item_praticien.xml}).
 * Il adapte dynamiquement la visibilité des champs selon le type de données à afficher.
 * </p>
 */
public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {

    /** Tableau JSON contenant les données à afficher dans la liste. */
    private JSONArray dataArray = new JSONArray();

    /**
     * Indicateur permettant de savoir quel type de données est actuellement traité.
     * {@code true} si la liste contient des départements, {@code false} pour des praticiens.
     */
    private boolean isDepartementList = false;

    /**
     * Constructeur par défaut de l'adaptateur.
     * Initialise un adaptateur vide avant que les données ne soient injectées via {@link #setData(JSONArray, boolean)}.
     */
    public ResultAdapter() {
    }

    /**
     * Met à jour le jeu de données de l'adaptateur et déclenche le rafraîchissement de la liste.
     *
     * @param dataArray         Le nouveau tableau {@link JSONArray} contenant les données récupérées de l'API.
     * @param isDepartementList Un booléen indiquant s'il s'agit d'une liste de départements ({@code true})
     *                          ou de praticiens ({@code false}).
     */
    @SuppressLint("NotifyDataSetChanged")
    public void setData(JSONArray dataArray, boolean isDepartementList) {
        this.dataArray = dataArray;
        this.isDepartementList = isDepartementList;
        notifyDataSetChanged();
    }

    /**
     * Crée de nouvelles vues (invoqué par le gestionnaire de mise en page du RecyclerView).
     *
     * @param parent   Le ViewGroup dans lequel la nouvelle vue sera ajoutée après avoir été liée à une position d'adaptateur.
     * @param viewType Le type de la nouvelle vue (inutilisé ici car nous n'avons qu'un seul layout).
     * @return Un nouveau {@link ViewHolder} qui contient la vue du layout {@code item_praticien}.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // On réutilise item_praticien pour tout, en masquant certains champs si c'est un département
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_praticien, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Remplace le contenu d'une vue (invoqué par le gestionnaire de mise en page du RecyclerView).
     * Extrait les données du {@link JSONArray} à la position donnée et met à jour le {@link ViewHolder}.
     *
     * @param holder   Le ViewHolder qui doit être mis à jour pour représenter le contenu de l'élément à la position donnée.
     * @param position La position de l'élément dans le jeu de données de l'adaptateur.
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            JSONObject obj = dataArray.getJSONObject(position);

            if (isDepartementList) {
                // Affichage d'un département
                String dept = obj.getString("DEPARTEMENT");
                holder.tvNomPrenom.setText("Département : " + dept);

                // Masquage des champs inutiles pour un département
                holder.tvSpecialite.setVisibility(View.GONE);
                holder.tvAdresse.setVisibility(View.GONE);
                holder.tvNotoriete.setVisibility(View.GONE);
            } else {
                // Affichage d'un praticien

                // Rétablissement de la visibilité des champs au cas où la vue est recyclée
                holder.tvSpecialite.setVisibility(View.VISIBLE);
                holder.tvAdresse.setVisibility(View.VISIBLE);
                holder.tvNotoriete.setVisibility(View.VISIBLE);

                String nom = obj.getString("PRA_NOM").toUpperCase();
                String prenom = obj.getString("PRA_PRENOM");
                holder.tvNomPrenom.setText(nom + " " + prenom);

                holder.tvSpecialite.setText(obj.getString("TYP_LIBELLE"));

                String adresse = obj.getString("PRA_ADRESSE") + "\n" +
                        obj.getString("PRA_CP") + " " + obj.getString("PRA_VILLE");
                holder.tvAdresse.setText(adresse);

                holder.tvNotoriete.setText("Notoriété : " + obj.getString("PRA_COEFNOTORIETE"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retourne la taille totale du jeu de données conservé par l'adaptateur.
     *
     * @return Le nombre total d'éléments dans le {@link JSONArray}.
     */
    @Override
    public int getItemCount() {
        return dataArray.length();
    }

    /**
     * Classe interne représentant le ViewHolder.
     * Conserve les références vers les différents composants visuels (TextViews) d'un élément de la liste
     * pour éviter d'avoir à faire des {@code findViewById} coûteux lors du défilement.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        /** TextView affichant le nom et le prénom du praticien, ou le numéro du département. */
        TextView tvNomPrenom;

        /** TextView affichant la spécialité (libellé du type) du praticien. */
        TextView tvSpecialite;

        /** TextView affichant l'adresse complète (rue, code postal, ville) du praticien. */
        TextView tvAdresse;

        /** TextView affichant le coefficient de notoriété du praticien. */
        TextView tvNotoriete;

        /**
         * Constructeur du ViewHolder.
         * Relie les attributs de la classe aux vues XML correspondantes.
         *
         * @param itemView La vue racine représentant un élément de la liste.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNomPrenom = itemView.findViewById(R.id.tv_nom_prenom);
            tvSpecialite = itemView.findViewById(R.id.tv_specialite);
            tvAdresse = itemView.findViewById(R.id.tv_adresse);
            tvNotoriete = itemView.findViewById(R.id.tv_notoriete);
        }
    }
}