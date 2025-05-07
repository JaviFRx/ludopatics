package com.example.ludopatics;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TopTenAdapter extends RecyclerView.Adapter<TopTenAdapter.ViewHolder> {

    private final List<Puntuacion> lista;

    public TopTenAdapter(List<Puntuacion> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public TopTenAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_top_ten, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull TopTenAdapter.ViewHolder holder, int position) {
        Puntuacion puntuacion = lista.get(position);
        holder.tvNombre.setText((position + 1) + ". " + puntuacion.nombre);
        holder.tvPuntos.setText(String.valueOf(puntuacion.puntuacion));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvPuntos;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreJugador);
            tvPuntos = itemView.findViewById(R.id.tvPuntosJugador);
        }
    }
}