package com.example.devandroid; // À adapter

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {

    private JSONArray dataArray = new JSONArray();
    private boolean isDepartementList = false;

    // Met à jour les données et rafraîchit la liste
    public void setData(JSONArray dataArray, boolean isDepartementList) {
        this.dataArray = dataArray;
        this.isDepartementList = isDepartementList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // On réutilise item_praticien pour tout, en masquant certains champs si c'est un département
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_praticien, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            JSONObject obj = dataArray.getJSONObject(position);

            if (isDepartementList) {
                // Affichage d'un département
                String dept = obj.getString("DEPARTEMENT");
                holder.tvNomPrenom.setText("Département : " + dept);
                holder.tvSpecialite.setVisibility(View.GONE);
                holder.tvAdresse.setVisibility(View.GONE);
                holder.tvNotoriete.setVisibility(View.GONE);
            } else {
                // Affichage d'un praticien
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

    @Override
    public int getItemCount() {
        return dataArray.length();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNomPrenom, tvSpecialite, tvAdresse, tvNotoriete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNomPrenom = itemView.findViewById(R.id.tv_nom_prenom);
            tvSpecialite = itemView.findViewById(R.id.tv_specialite);
            tvAdresse = itemView.findViewById(R.id.tv_adresse);
            tvNotoriete = itemView.findViewById(R.id.tv_notoriete);
        }
    }
}