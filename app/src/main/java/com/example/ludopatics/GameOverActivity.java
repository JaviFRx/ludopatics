package com.example.ludopatics;

import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GameOverActivity extends AppCompatActivity {
    String nombreUsuario = null;
    public static MediaPlayer mediaPlayer2;

    // Audio Focus
    private AudioManager audioManager;
    private AudioFocusRequest focusRequest;

    // Soporte de idioma
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        // Obtener los datos enviados desde la actividad anterior
        int roundCount = getIntent().getIntExtra("roundCount", 0);
        int currentBalance = getIntent().getIntExtra("currentBalance", 0);
        nombreUsuario = getIntent().getStringExtra("nombreUsuario");
        if (nombreUsuario == null) {
            nombreUsuario = "Invitado";
        }

        // Mensaje final
        TextView messageTextView = findViewById(R.id.game_over_message);
        messageTextView.setText("Juego terminado\nRondas jugadas: " + roundCount + "\nSaldo restante: " + currentBalance);

        // Botón reiniciar
        Button restartButton = findViewById(R.id.restartButton);
        restartButton.setOnClickListener(v -> {
            if (MainActivity.mediaPlayer2 != null) {
                if (MainActivity.mediaPlayer2.isPlaying()) {
                    MainActivity.mediaPlayer2.stop();
                }
                MainActivity.mediaPlayer2.release();
                MainActivity.mediaPlayer2 = null;
            }
            Intent intent = new Intent(GameOverActivity.this, MainActivity.class);
            intent.putExtra("nombreUsuario", nombreUsuario);
            startActivity(intent);
            finish();
        });

        // Botón salir
        Button exitButton = findViewById(R.id.exitButton);
        exitButton.setOnClickListener(v -> {
            if (MainActivity.mediaPlayer2 != null) {
                if (MainActivity.mediaPlayer2.isPlaying()) {
                    MainActivity.mediaPlayer2.stop();
                }
                MainActivity.mediaPlayer2.release();
                MainActivity.mediaPlayer2 = null;
            }
            Intent intent = new Intent(GameOverActivity.this, Menu.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("nombreUsuario", nombreUsuario);
            startActivity(intent);
            finish();
        });

        // Configurar enfoque de audio
        configurarAudioFocus();
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
                                if (MainActivity.mediaPlayer2 != null && MainActivity.mediaPlayer2.isPlaying()) {
                                    MainActivity.mediaPlayer2.pause();
                                }
                                break;
                            case AudioManager.AUDIOFOCUS_GAIN:
                                if (MainActivity.mediaPlayer2 != null && !MainActivity.mediaPlayer2.isPlaying()) {
                                    MainActivity.mediaPlayer2.start();
                                }
                                break;
                        }
                    })
                    .build();

            int result = audioManager.requestAudioFocus(focusRequest);
            if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                // AudioFocus no concedido
            }
        }
    }
}
