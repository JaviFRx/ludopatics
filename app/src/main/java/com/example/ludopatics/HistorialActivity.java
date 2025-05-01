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

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        // Acción para el botón de volver hacia atras: cierra la Activity
        btnBack.setOnClickListener(v -> finish());

        recyclerHistorial.setLayoutManager(new LinearLayoutManager(this));

        // Iniciamos DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Obtener el ID del usuario
        String nombreUsuario = getIntent().getStringExtra("nombreUsuario");
        tvTittle.setText("HISTORIAL DE: " + nombreUsuario);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        String campoFiltro;
        String valorFiltro;

// Si el usuario está logueado, usamos el UID
        if (auth.getCurrentUser() != null) {
            campoFiltro = "uid";
            valorFiltro = auth.getCurrentUser().getUid();
        } else {
            campoFiltro = "nombre";
            valorFiltro = nombreUsuario;
        }

        db.collection("puntuaciones")
                .whereEqualTo(campoFiltro, valorFiltro)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Partida> historialList = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String nombre = doc.getString("nombre");
                        Long puntuacion = doc.getLong("puntuacion");
                        Timestamp ts = doc.getTimestamp("timestamp");

                        String fechaFormateada = ts != null
                                ? new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(ts.toDate())
                                : "Sin fecha";

                        if (nombre != null && puntuacion != null) {
                            historialList.add(new Partida(0, 0, fechaFormateada, puntuacion.intValue()));
                        }
                    }

                    HistorialAdapter adapter = new HistorialAdapter(historialList);
                    recyclerHistorial.setAdapter(adapter);

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al obtener historial desde Firebase", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });

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
