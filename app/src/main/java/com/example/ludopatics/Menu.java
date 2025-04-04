package com.example.ludopatics;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

import java.util.Locale;

public class Menu extends AppCompatActivity {

    // Declarar las vistas globalmente para poder acceder a ellas en cualquier parte de la clase
    private TextView tvUsername, tvTitle;
    private Button btnJugarSolo, btnMultijugador, btnHistorial, btnMiPerfil, btnComoJugar, btnExit;
    private ImageView imgEngland, imgFrance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Se establece el layout activity_usuario.xml, que tiene el id "main" en la raíz
        setContentView(R.layout.activity_menu);

        // Configuración de EdgeToEdge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Obtener el nombre del usuario del Intent
        String nombreUsuario = getIntent().getStringExtra("nombreUsuario");

        // Inicializar las vistas
        tvUsername = findViewById(R.id.tvUsername);
        tvTitle = findViewById(R.id.tvTitle);
        btnJugarSolo = findViewById(R.id.btnJugarSolo);
        btnMultijugador = findViewById(R.id.btnMultijugador);
        btnHistorial = findViewById(R.id.btnHistorial);
        btnMiPerfil = findViewById(R.id.btnMiPerfil);
        btnComoJugar = findViewById(R.id.btnComoJugar);
        imgEngland = findViewById(R.id.imgEngland);
        imgFrance = findViewById(R.id.imgFrance);

        // Establecer el nombre del usuario
        tvUsername.setText(nombreUsuario);

        // Asignamos listeners a los botones
        imgEngland.setOnClickListener(v -> changeLanguage("en"));  // Cambia el idioma a inglés
        imgFrance.setOnClickListener(v -> changeLanguage("fr"));  // Cambia el idioma a francés

        //btnExit.setOnClickListener(v -> finish());

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
            intent.putExtra("nombreUsuario", nombreUsuario);
            startActivity(intent);
        });

        btnMiPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(Menu.this, PerfilActivity.class);
            intent.putExtra("nombreUsuario", nombreUsuario);  // Pasa el nombre al perfil
            startActivity(intent);
        });

        btnComoJugar.setOnClickListener(v -> {
            Intent intent = new Intent(Menu.this, ComoJugarActivity.class);
            startActivity(intent);
        });
    }

    private void changeLanguage(String languageCode) {
        // Establecer el idioma de la aplicación
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        // Actualizar la configuración global de la aplicación
        Configuration config = getResources().getConfiguration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // Actualizar los textos de la interfaz
        updateUI();
    }

    private void updateUI() {
        // Actualiza los textos de la interfaz usando getString()
        tvTitle.setText(getString(R.string.app_name)); // Cambio de texto
        btnJugarSolo.setText(getString(R.string.jugar_solo));
        btnMultijugador.setText(getString(R.string.multijugador));
        btnHistorial.setText(getString(R.string.historial));
        btnMiPerfil.setText(getString(R.string.mi_perfil));
        btnComoJugar.setText(getString(R.string.como_jugar));
        //btnExit.setText(getString(R.string.exit));
    }
}
