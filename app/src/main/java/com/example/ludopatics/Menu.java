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

public class Menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Se establece el layout activity_usuario.xml, que tiene el id "main" en la raíz
        setContentView(R.layout.activity_menu);

        // Configuración de EdgeToEdge (si lo deseas, puede ir después de setContentView)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        String nombreUsuario = getIntent().getStringExtra("nombreUsuario");
        // "Escaneamos" (obtenemos las referencias) de los botones y otros views:
        Button btnExit = findViewById(R.id.btnExit);
        TextView tvUsername = findViewById(R.id.tvUsername);
        TextView tvTitle = findViewById(R.id.tvTitle);
        Button btnJugarSolo = findViewById(R.id.btnJugarSolo);
        Button btnMultijugador = findViewById(R.id.btnMultijugador);
        Button btnHistorial = findViewById(R.id.btnHistorial);
        Button btnMiPerfil = findViewById(R.id.btnMiPerfil);
        Button btnComoJugar = findViewById(R.id.btnComoJugar);

        TextView tvGifPlaceholder = findViewById(R.id.tvGifPlaceholder);
        tvUsername.setText(nombreUsuario);

        // Ejemplo: asignar listeners a los botones
        btnExit.setOnClickListener(v -> {
            // Cierra la Activity o realiza otra acción
            finish();
        });

        btnJugarSolo.setOnClickListener(v -> {
            Intent intent = new Intent(Menu.this, MainActivity.class);
            intent.putExtra("nombreUsuario", nombreUsuario);  // Enviar el nombre al intent

            startActivity(intent);
        });


        btnMultijugador.setOnClickListener(v -> {
            // Lógica para "MULTIJUGADOR"
            Intent intent = new Intent(Menu.this, MainActivity.class);
            startActivity(intent);
        });

        btnHistorial.setOnClickListener(v -> {
            Intent intent = new Intent(Menu.this, HistorialActivity.class);
            intent.putExtra("nombreUsuario", nombreUsuario); // <-- ESTA LÍNEA FALTABA
            startActivity(intent);
        });


        btnMiPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(Menu.this, PerfilActivity.class);
            intent.putExtra("nombreUsuario", nombreUsuario);  // Pasa el nombre al perfil
            startActivity(intent);
        });

    }
}
