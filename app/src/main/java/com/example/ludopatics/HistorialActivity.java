package com.example.ludopatics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HistorialActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private RecyclerView recyclerHistorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        // Obtener referencias de las vistas
        btnBack = findViewById(R.id.btnBack);
        recyclerHistorial = findViewById(R.id.recyclerHistorial);

        // Acción para el botón de retroceso: cierra la Activity
        btnBack.setOnClickListener(v -> finish());

        // Configuramos el RecyclerView con un LinearLayoutManager
        recyclerHistorial.setLayoutManager(new LinearLayoutManager(this));

        // Creamos algunos datos de ejemplo para el historial
        List<String> historialData = new ArrayList<>();
        historialData.add("Partida 1: Ganó 100");
        historialData.add("Partida 2: Perdió 50");
        historialData.add("Partida 3: Empató");
        historialData.add("Partida 4: Ganó 200");

        // Creamos el adaptador y se lo asignamos al RecyclerView
        HistorialAdapter adapter = new HistorialAdapter(historialData);
        recyclerHistorial.setAdapter(adapter);
    }

    // Declara la clase HistorialAdapter como una clase anidada estática,
    // pero asegúrate de que esté fuera de cualquier método, directamente en la clase HistorialActivity.
    public static class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.ViewHolder> {

        private final List<String> data;

        public HistorialAdapter(List<String> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public HistorialAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Inflamos el layout personalizado para cada ítem
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_historial, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull HistorialAdapter.ViewHolder holder, int position) {
            String record = data.get(position);
            holder.tvItem.setText(record);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvItem;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvItem = itemView.findViewById(R.id.tvItem);
            }
        }
    }
}
