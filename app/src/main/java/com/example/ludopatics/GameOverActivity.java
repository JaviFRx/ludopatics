package com.example.ludopatics;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GameOverActivity extends AppCompatActivity {
    String nombreUsuario=null;
    public static MediaPlayer mediaPlayer2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        // Obtener los datos enviados desde la actividad anterior
        int roundCount = getIntent().getIntExtra("roundCount", 0);
        int currentBalance = getIntent().getIntExtra("currentBalance", 0);
        nombreUsuario = getIntent().getStringExtra("nombreUsuario");
        if (nombreUsuario == null) {
            nombreUsuario = "Invitado"; // Valor por defecto
        }
        // Mostrar el mensaje de fin de juego
        TextView messageTextView = findViewById(R.id.game_over_message);
        messageTextView.setText("Juego terminado\nRondas jugadas: " + roundCount + "\nSaldo restante: " + currentBalance);
        Button restartButton = findViewById(R.id.restartButton);
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.mediaPlayer2 != null) {
                    if (MainActivity.mediaPlayer2.isPlaying()) {
                        MainActivity.mediaPlayer2.stop(); // Detiene la reproducción
                    }
                    MainActivity.mediaPlayer2.release(); // Libera recursos
                    MainActivity.mediaPlayer2 = null; // Evita referencias innecesarias
                }
                Intent intent = new Intent(GameOverActivity.this, MainActivity.class);
                intent.putExtra("nombreUsuario", nombreUsuario);
                startActivity(intent);
                finish();
            }
        });

        Button exitButton = findViewById(R.id.exitButton);
        exitButton.setOnClickListener(v -> {
            if (MainActivity.mediaPlayer2 != null) {
                if (MainActivity.mediaPlayer2.isPlaying()) {
                    MainActivity.mediaPlayer2.stop();
                }
                MainActivity.mediaPlayer2.release();
                MainActivity.mediaPlayer2 = null;
            }

            // Volver a la actividad de menú principal
            Intent intent = new Intent(GameOverActivity.this, Menu.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("nombreUsuario", nombreUsuario); // Por si necesitas conservar el nombre
            startActivity(intent);
            finish(); // Finaliza esta actividad
        });
    }
    }

