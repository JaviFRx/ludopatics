package com.example.ludopatics;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

public class usuario extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Se establece el layout activity_usuario.xml, que tiene el id "main" en la raíz
        setContentView(R.layout.activity_usuario);

        // Configuración de EdgeToEdge (si lo deseas, puede ir después de setContentView)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // "Escaneamos" (obtenemos las referencias) de los botones y otros views:
        Button btnExit = findViewById(R.id.btnExit);
        TextView tvUsername = findViewById(R.id.tvUsername);
        TextView tvTitle = findViewById(R.id.tvTitle);
        Button btnJugarSolo = findViewById(R.id.btnJugarSolo);
        Button btnMultijugador = findViewById(R.id.btnMultijugador);
        Button btnHistorial = findViewById(R.id.btnHistorial);
        Button btnMiPerfil = findViewById(R.id.btnMiPerfil);
        TextView tvGifPlaceholder = findViewById(R.id.tvGifPlaceholder);

        // Ejemplo: asignar listeners a los botones
        btnExit.setOnClickListener(v -> {
            // Cierra la Activity o realiza otra acción
            finish();
        });

        btnJugarSolo.setOnClickListener(v -> {
            Intent intent = new Intent(usuario.this, MainActivity.class);
            startActivity(intent);
        });


        btnMultijugador.setOnClickListener(v -> {
            // Lógica para "MULTIJUGADOR"
            Intent intent = new Intent(usuario.this, MainActivity.class);
            startActivity(intent);
        });

        btnHistorial.setOnClickListener(v -> {
            // Lógica para "HISTORIAL"
            Intent intent = new Intent(usuario.this, HistorialActivity.class);
            startActivity(intent);        });

        btnMiPerfil.setOnClickListener(v -> {
            // Lógica para "MI PERFIL"
            Log.d("usuario", "Se pulsó MI PERFIL");
        });
    }
}
