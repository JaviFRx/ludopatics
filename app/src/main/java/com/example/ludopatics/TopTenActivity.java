package com.example.ludopatics;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TopTenActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TopTenAdapter adapter;
    private List<Puntuacion> listaTopTen = new ArrayList<>();
    private FirebaseFirestore db;
    private TextView tvPremioComun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_ten);

        recyclerView = findViewById(R.id.recyclerTopTen);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TopTenAdapter(listaTopTen);
        recyclerView.setAdapter(adapter);

        tvPremioComun = findViewById(R.id.tvPremioComun);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        db = FirebaseFirestore.getInstance();
        cargarTopTen();
    }

    private void cargarTopTen() {
        // Primero cargamos el ranking de puntuaciones
        db.collection("puntuaciones")
                .orderBy("puntuacion", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaTopTen.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String nombre = doc.getString("nombre");
                        Long puntos = doc.getLong("puntuacion");
                        if (nombre != null && puntos != null) {
                            listaTopTen.add(new Puntuacion("", nombre, puntos.intValue()));
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar el ranking", Toast.LENGTH_SHORT).show();
                    Log.e("TOP10", "Error:", e);
                });

        // Luego cargamos el valor del bote
        db.collection("bote")
                .document("valor")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long boteValor = documentSnapshot.getLong("bote");
                        if (boteValor != null) {
                            tvPremioComun.setText(getString(R.string.premio_comun, boteValor));
                        } else {
                            tvPremioComun.setText(getString(R.string.premio_comun, 0));
                        }
                    } else {
                        tvPremioComun.setText(getString(R.string.premio_comun, 0));
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar el bote", Toast.LENGTH_SHORT).show();
                    Log.e("BOTE", "Error:", e);
                });
    }

}
