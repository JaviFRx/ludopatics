package com.example.ludopatics;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.app.AlertDialog;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private String selectedColor = "";
    private String resColor = "";
    private ImageView ruletaImage;
    private TextView apuestaTextView;

    private Button btnGirar;
    private TextView textViewNumero;
    private CirculosView circleView;
    private MediaPlayer mediaPlayer;

    private TextView balanceValue;
    private TextView betAmount;
    private Button betButtonPlus1;
    private Button betButtonPlus10;
    private Button betButtonPlus100;
    private Button placeBetButton;
    private Button numButton;

    private int currentBalance = 1000;
    private int currentBetAmount = 0;

    private String numeroSeleccionado = null;

    private final String[] casillasRuleta = {
            "10", "5", "24", "16", "33", "1", "20", "14", "31", "9", "22", "18",
            "29", "7", "28", "12", "35", "3", "26", "0", "32", "15", "19", "4",
            "21", "2", "25", "17", "34", "6", "27", "13", "36", "11", "30", "8", "23"
    };

    private final Set<String> numerosNegros = new HashSet<>(Arrays.asList(
            "1", "3", "5", "7", "9", "12", "14", "16", "18", "19", "21", "23", "25", "27", "30", "32", "34", "36"
    ));

    private final Set<String> numerosRojos = new HashSet<>(Arrays.asList(
            "2", "4", "6", "8", "10", "11", "13", "15", "17", "20", "22", "24", "26", "28", "29", "31", "33", "35"
    ));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ruletaImage = findViewById(R.id.ruletaImage);
        btnGirar = findViewById(R.id.btnGirar);
        textViewNumero = findViewById(R.id.textViewNumero);
        circleView = findViewById(R.id.circleView);
        apuestaTextView = findViewById(R.id.apuesta);

        balanceValue = findViewById(R.id.balanceValue);
        betAmount = findViewById(R.id.bet_amount);
        betButtonPlus1 = findViewById(R.id.bet_button_plus);
        betButtonPlus10 = findViewById(R.id.bet_button_plus10);
        betButtonPlus100 = findViewById(R.id.bet_button_plus100);
        placeBetButton = findViewById(R.id.place_bet_button);
        numButton = findViewById(R.id.num_button);

        balanceValue.setText(String.valueOf(currentBalance));
        updateBetAmountText();

        betButtonPlus1.setOnClickListener(view -> increaseBet(1));
        betButtonPlus10.setOnClickListener(view -> increaseBet(10));
        betButtonPlus100.setOnClickListener(view -> increaseBet(100));
        placeBetButton.setOnClickListener(view -> placeBet());
        btnGirar.setOnClickListener(view -> girarRuleta());
        numButton.setOnClickListener(view -> showNumberPickerDialog());
    }

    private void increaseBet(int amount) {
        if (currentBalance >= amount) {
            currentBalance -= amount;
            currentBetAmount += amount;
            balanceValue.setText(String.valueOf(currentBalance));
            updateBetAmountText();
        } else {
            Toast.makeText(this, "No tienes suficiente saldo", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateBetAmountText() {
        if (currentBetAmount > 0) {
            betAmount.setText("Bet Amount: " + currentBetAmount);
        } else {
            betAmount.setText("Bet Amount: minimum 1");
        }
    }

    private void placeBet() {
        if (currentBetAmount > 0) {
            Toast.makeText(this, "Apuesta colocada", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Debes hacer una apuesta primero", Toast.LENGTH_SHORT).show();
        }
    }

    private void showNumberPickerDialog() {
        final String[] numeros = new String[37];
        for (int i = 0; i <= 36; i++) {
            numeros[i] = String.valueOf(i);
        }
        new AlertDialog.Builder(this)
                .setTitle("Selecciona un número para apostar")
                .setItems(numeros, (dialog, which) -> {
                    numeroSeleccionado = numeros[which];
                    Toast.makeText(this, "Has seleccionado el número: " + numeroSeleccionado, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void girarRuleta() {
        if (currentBetAmount <= 0) {
            Toast.makeText(this, "Debes hacer una apuesta primero", Toast.LENGTH_SHORT).show();
            return;
        }

        reproducirSonido();

        Random random = new Random();
        int vueltas = 5;
        int angle = random.nextInt(360) + (360 * vueltas);

        RotateAnimation rotateAnimation = new RotateAnimation(0, angle,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(10000);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());

        rotateAnimation.setAnimationListener(new android.view.animation.Animation.AnimationListener() {
            @Override
            public void onAnimationStart(android.view.animation.Animation animation) {
                betButtonPlus1.setEnabled(false);
                betButtonPlus10.setEnabled(false);
                betButtonPlus100.setEnabled(false);
                placeBetButton.setEnabled(false);
                btnGirar.setEnabled(false);
            }

            @Override
            public void onAnimationEnd(android.view.animation.Animation animation) {
                calcularCasilla(angle);
                betButtonPlus1.setEnabled(true);
                betButtonPlus10.setEnabled(true);
                betButtonPlus100.setEnabled(true);
                placeBetButton.setEnabled(true);
                btnGirar.setEnabled(true);
                currentBetAmount = 0;
                updateBetAmountText();
            }

            @Override
            public void onAnimationRepeat(android.view.animation.Animation animation) {}
        });

        ruletaImage.startAnimation(rotateAnimation);
    }

    private void reproducirSonido() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer = MediaPlayer.create(this, R.raw.efecto);
        mediaPlayer.start();
    }

    private void calcularCasilla(int angle) {
        int normalizedAngle = angle % 360;
        int numCasillas = casillasRuleta.length;
        double anguloPorCasilla = 360.0 / numCasillas;
        int indice = (numCasillas - 1 - (int) (normalizedAngle / anguloPorCasilla)) % numCasillas;
        String casillaFinal = casillasRuleta[indice];

        textViewNumero.setText("Número: " + casillaFinal);
        circleView.setCircleColor(Color.RED);
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}
