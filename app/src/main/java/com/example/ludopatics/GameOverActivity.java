package com.example.ludopatics;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GameOverActivity extends AppCompatActivity {
    String nombreUsuario=null;
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
                Intent intent = new Intent(GameOverActivity.this, MainActivity.class);
                intent.putExtra("nombreUsuario", nombreUsuario);
                startActivity(intent);
                finish();
            }
        });
            }
    }

