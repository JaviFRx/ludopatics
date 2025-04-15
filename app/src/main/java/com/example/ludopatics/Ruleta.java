package com.example.ludopatics;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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

    private AudioManager audioManager;
    private AudioFocusRequest focusRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ruleta);

        //permiso de acceso a localización
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        //permiso de acceso a calendario
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR},
                    2);
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

        // Reproducir audio en bucle con buenas prácticas multimedia
        mediaPlayer = MediaPlayer.create(this, R.raw.ludopat);
        if (mediaPlayer != null) {
            mediaPlayer.setLooping(true);
            configurarAudioFocus();
        }

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
                // Verificar si el nombre ya existe
                if (dbHelper.existeNombre(nombre) || dbHelper.guardarNombre(nombre)) {
                    Log.i("Database", "Nombre válido (nuevo o existente), entrando a la app...");

                    // Cambiar a la siguiente actividad
                    Intent intent = new Intent(Ruleta.this, Menu.class);
                    intent.putExtra("nombreUsuario", nombre);  // Enviar el nombre al intent
                    startActivity(intent);
                    finish();
                } else {
                    Log.e("Database", "No se pudo guardar el nombre en la base de datos");
                    Toast.makeText(this, "Error al guardar el nombre", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Por favor, ingresa un nombre", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void configurarAudioFocus() {
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(audioAttributes)
                    .setOnAudioFocusChangeListener(focusChange -> {
                        switch (focusChange) {
                            case AudioManager.AUDIOFOCUS_LOSS:
                            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                                    mediaPlayer.pause();
                                }
                                break;
                            case AudioManager.AUDIOFOCUS_GAIN:
                                if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                                    mediaPlayer.start();
                                }
                                break;
                        }
                    })
                    .build();

            int result = audioManager.requestAudioFocus(focusRequest);
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mediaPlayer.start();
            } else {
                Log.e("Ruleta", "No se pudo obtener el AudioFocus");
            }
        } else {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (audioManager != null && focusRequest != null) {
            audioManager.abandonAudioFocusRequest(focusRequest);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 2) {
            boolean permisosConcedidos = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    permisosConcedidos = false;
                    break;
                }
            }

            if (permisosConcedidos) {
                addEventCalendar.insertarVictoria(this);
            } else {
                Toast.makeText(this, "Se necesitan permisos de calendario", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
