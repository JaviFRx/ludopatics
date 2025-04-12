package com.example.ludopatics;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.Manifest;

public class Ruleta extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private EditText etNombreUsuario;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ruleta);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configurar VideoView
        VideoView videoView = findViewById(R.id.videoView);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ruleta);
        videoView.setVideoURI(videoUri);

        // Reproducir video en bucle automáticamente
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            videoView.start();
        });

        // Reproducir audio en bucle
        mediaPlayer = MediaPlayer.create(this, R.raw.ludopat);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        // Instanciar la base de datos
        dbHelper = new DatabaseHelper(this);

        // Configurar EditText para ingresar nombre
        etNombreUsuario = findViewById(R.id.etNombreUsuario);

        // Cargar nombre si ya está en la base de datos
        String nombreGuardado = dbHelper.obtenerNombre();
        etNombreUsuario.setText(nombreGuardado);

        // Botón "JUGAR"
        Button btnJugar = findViewById(R.id.btnJugar);
        btnJugar.setOnClickListener(v -> {
            String nombre = etNombreUsuario.getText().toString().trim(); // Asegurar que obtenemos el nombre

            if (!nombre.isEmpty()) {
                // Guardar el nombre en la base de datos
                if (dbHelper.guardarNombre(nombre)) {
                    Log.i("Database", "El nombre se guardó exitosamente en la BD");

                    // Cambiar a la siguiente actividad
                    Intent intent = new Intent(Ruleta.this, Menu.class);
                    intent.putExtra("nombreUsuario", nombre);  // Enviar el nombre al intent
                    startActivity(intent);
                    finish();
                } else {
                    Log.e("Database", "No se pudo guardar el nombre en la BD");
                    Toast.makeText(this, "Error al guardar el nombre", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Mostrar mensaje si el nombre está vacío
                Toast.makeText(this, "Por favor, ingresa un nombre", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
