package com.example.ludopatics;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Ruleta extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private EditText etNombreUsuario;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ruleta);

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
            String nombre = etNombreUsuario.getText().toString().trim();
            if (!nombre.isEmpty()) {
                boolean guardado = dbHelper.guardarNombre(nombre);
                if (guardado) {
                    Toast.makeText(this, "Nombre guardado: " + nombre, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(Ruleta.this, MainActivity.class);
                intent.putExtra("nombreUsuario", nombre);  // Enviar el nombre al intent
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Introduce tu nombre", Toast.LENGTH_SHORT).show();
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
