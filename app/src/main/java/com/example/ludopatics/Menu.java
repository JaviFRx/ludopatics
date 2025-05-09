package com.example.ludopatics;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;
import java.util.Locale;

public class Menu extends AppCompatActivity {

    // Declarar las vistas globalmente para poder acceder a ellas en cualquier parte de la clase
    private TextView tvUsername, tvTitle,tvCoordinates;
    private Button btnJugarSolo, btnMultijugador, btnHistorial, btnMiPerfil, btnComoJugar, btnSeleccionarMusica, btnExit;
    private ImageView imgEngland, imgFrance, imgSpain,imgJapan;
    private ActivityResultLauncher<Intent> audioPickerLauncher;
    private String coordenadasTexto;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase));
    }

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
        btnSeleccionarMusica = findViewById(R.id.btnSeleccionarMusica);
        btnExit = findViewById(R.id.btnExit);
        imgEngland = findViewById(R.id.imgEngland);
        imgFrance = findViewById(R.id.imgFrance);
        imgSpain = findViewById(R.id.imgSpain);
        imgJapan = findViewById(R.id.imgJapan);
        tvCoordinates = findViewById(R.id.tvCoordinates);
        // Establecer el nombre del usuario
        tvUsername.setText(nombreUsuario);


        // Asignamos listeners a los botones
        imgEngland.setOnClickListener(v -> changeLanguage("en"));  // Cambia el idioma a inglés
        imgFrance.setOnClickListener(v -> changeLanguage("fr"));  // Cambia el idioma a francés
        imgSpain.setOnClickListener(v -> changeLanguage("es"));  // Cambia el idioma a francés
        imgJapan.setOnClickListener(v -> changeLanguage("ja"));
        btnExit.setOnClickListener(v -> finish());
        //btnExit.setOnClickListener(v -> finish());
        audioPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri audioUri = result.getData().getData();

                        // Persistir permisos de acceso
                        final int takeFlags = result.getData().getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        getContentResolver().takePersistableUriPermission(audioUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        // Guardar URI en SharedPreferences
                        SharedPreferences prefs = getSharedPreferences("ludopatics", MODE_PRIVATE);
                        prefs.edit().putString("custom_music_uri", audioUri.toString()).apply();

                        }
                }
        );

// Botón para seleccionar música
        btnSeleccionarMusica.setOnClickListener(v -> {
            // Crear un Intent para abrir el selector de música
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("audio/*"); // Solo seleccionará archivos de tipo audio
            intent.addCategory(Intent.CATEGORY_OPENABLE); // Permitir que el archivo sea abierto
            audioPickerLauncher.launch(intent); // Lanza el picker
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

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        // Obtener las coordenadas del usuario de la base de datos
        float[] coordenadas = dbHelper.obtenerCoordenadas(nombreUsuario);
        Locale locale = Locale.getDefault(); // o Locale.ROOT si prefieres un formato independiente de idioma

        if (coordenadas != null) {
            // Si las coordenadas existen, mostrarlas en el TextView
            float latitud = coordenadas[0];
            float longitud = coordenadas[1];

            // Formatear el texto de las coordenadas y asignarlo al TextView
            // Usar String.format para dar formato a los números flotantes
            coordenadasTexto = String.format(locale, getString(R.string.coordenadas), String.format(locale, "%.6f", latitud), String.format(locale, "%.6f", longitud));
            tvCoordinates.setText(coordenadasTexto);
        } else {
            // Si no se encontraron coordenadas, mostrar un mensaje de error
            tvCoordinates.setText("No se encontraron coordenadas para este usuario.");
        }
    }

    private void changeLanguage(String languageCode) {
        // Guardar idioma en SharedPreferences
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("App_Lang", languageCode);
        editor.apply();

        // Reiniciar actividad para aplicar idioma
        recreate(); // Esto recarga la UI con el nuevo contexto
    }


    private void updateUI() {
        // Actualiza los textos de la interfaz usando getString()
        tvTitle.setText(getString(R.string.app_name)); // Cambio de texto
        btnJugarSolo.setText(getString(R.string.jugar_solo));
        btnMultijugador.setText(getString(R.string.multijugador));
        btnHistorial.setText(getString(R.string.historial));
        btnMiPerfil.setText(getString(R.string.mi_perfil));
        btnComoJugar.setText(getString(R.string.como_jugar));
        btnExit.setText(getString(R.string.exit));
        btnSeleccionarMusica.setText(getString(R.string.select_music));
        tvCoordinates.setText(coordenadasTexto);
    }
}
