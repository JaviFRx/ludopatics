package com.example.ludopatics;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class SplashActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Encuentra el ImageView en la pantalla de inicio y establece el GIF usando Glide
        ImageView splashImage = findViewById(R.id.splashImage);

        // Cargar el GIF con Glide
        Glide.with(this)
                .load(R.drawable.presents)
                .into(splashImage);

        // Inicializar y reproducir el audio
        mediaPlayer = MediaPlayer.create(this, R.raw.century);
        mediaPlayer.start();

        // Esperar a que termine el audio antes de abrir Ruleta
        mediaPlayer.setOnCompletionListener(mp -> {
            // Liberar el MediaPlayer
            mp.release();
            mediaPlayer = null;

            // Cambiar a la actividad Ruleta
            Intent intent = new Intent(SplashActivity.this, Ruleta.class);
            startActivity(intent);
            finish(); // Cierra SplashActivity para que no pueda volver a ella
        });
    }

    @Override
    protected void onDestroy() {
        // Asegurarse de liberar el MediaPlayer al destruir la actividad
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}