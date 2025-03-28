package com.example.ludopatics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistorialActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private RecyclerView recyclerHistorial;
    private TextView tvTittle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);


        // Obtener referencias de las vistas
        ImageButton btnBack = findViewById(R.id.btnBack);
        recyclerHistorial = findViewById(R.id.recyclerHistorial);
        tvTittle = findViewById(R.id.tvTitle);
        // Acción para el botón de retroceso: cierra la Activity
        btnBack.setOnClickListener(v -> finish());

        // Configuramos el RecyclerView con un LinearLayoutManager
        recyclerHistorial.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Obtener el ID del usuario (ajustar según cómo obtienes el nombre)
        String nombreUsuario = getIntent().getStringExtra("NOMBRE_USUARIO");
        tvTittle.setText("HISTORIAL DE: " + nombreUsuario);
        int usuarioId = dbHelper.obtenerIdJugador(nombreUsuario);

        // Verificar que usuarioId es válido
        if (usuarioId != -1) {
            // Obtener historial de partidas
            List<Partida> historialList = dbHelper.obtenerPartidas(usuarioId);

            // Crear y asignar el adaptador al RecyclerView
            HistorialAdapter adapter = new HistorialAdapter(historialList);
            recyclerHistorial.setAdapter(adapter);
        } else {
            Toast.makeText(this, "No se encontró el jugador.", Toast.LENGTH_SHORT).show();
        }
    }

    // Adaptador para mostrar el historial de partidas
    public static class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.ViewHolder> {

        private final List<Partida> data;

        public HistorialAdapter(List<Partida> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public HistorialAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Inflar el layout del item de historial
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_historial, parent, false);
            return new ViewHolder(view);
        }

        public void onBindViewHolder(@NonNull HistorialAdapter.ViewHolder holder, int position) {
            Partida partida = data.get(position);

            // Mostrar la información de la partida (ID, fecha, y saldo final)
            holder.tvPartidaId.setText("Partida: " + partida.getIdPartida());
            holder.tvFecha.setText("Fecha: " + partida.getFecha());
            holder.tvSaldo.setText("Saldo Final: " + partida.getSaldoFinal());
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvPartidaId, tvFecha, tvSaldo;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvPartidaId = itemView.findViewById(R.id.tvPartidaId);
                tvFecha = itemView.findViewById(R.id.tvFecha);
                tvSaldo = itemView.findViewById(R.id.tvSaldo);
            }
        }
    }
}
