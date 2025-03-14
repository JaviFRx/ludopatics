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
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private String selectedColor = ""; // Variable para almacenar el color apostado
    private String resColor = "";
    private ImageView ruletaImage;
    private TextView apuestaTextView;
    private Button btnGirar;
    private TextView textViewNumero;
    private CirculosView circleView;
    private MediaPlayer mediaPlayer;

    // Variables para manejar las apuestas
    private TextView balanceValue;
    private TextView betAmount;
    private Button betButtonPlus1;
    private Button betButtonPlus10;
    private Button betButtonPlus100;
    private Button placeBetButton;
    private Button num_Button;

    // Variables para el seguimiento de la apuesta y el saldo
    private int currentBalance = 1000; // Saldo inicial
    private int currentBetAmount = 0; // Cantidad de apuesta actual
    private int numeroSeleccionado = -1;
    // Definimos los números de la ruleta en orden
    private final String[] casillasRuleta = {
            "10", "5", "24", "16", "33", "1", "20", "14", "31", "9", "22", "18",
            "29", "7", "28", "12", "35", "3", "26", "0", "32", "15", "19", "4",
            "21", "2", "25", "17", "34", "6", "27", "13", "36", "11", "30", "8", "23"
    };

    // Conjuntos para definir los colores de los números
    private final Set<String> numerosNegros = new HashSet<>(Arrays.asList(
            "1", "3", "5", "7", "9", "12", "14", "16", "18", "19", "21", "23", "25", "27", "30", "32", "34", "36"
    ));

    private final Set<String> numerosRojos = new HashSet<>(Arrays.asList(
            "2", "4", "6", "8", "10", "11", "13", "15", "17", "20", "22", "24", "26", "28", "29", "31", "33", "35"
    ));

    // El 0 es verde

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

        // Inicializar las vistas
        ruletaImage = findViewById(R.id.ruletaImage);
        btnGirar = findViewById(R.id.btnGirar);
        textViewNumero = findViewById(R.id.textViewNumero);
        circleView = findViewById(R.id.circleView);

        apuestaTextView = findViewById(R.id.apuesta);
        // Inicializar las vistas relacionadas con las apuestas
        balanceValue = findViewById(R.id.balanceValue);
        betAmount = findViewById(R.id.bet_amount);
        betButtonPlus1 = findViewById(R.id.bet_button_plus);
        betButtonPlus10 = findViewById(R.id.bet_button_plus10);
        betButtonPlus100 = findViewById(R.id.bet_button_plus100);
        placeBetButton = findViewById(R.id.place_bet_button);

        // Configurar el saldo inicial
        balanceValue.setText(String.valueOf(currentBalance));

        // Configurar el texto inicial de la apuesta
        updateBetAmountText();

        // Configurar los listeners para los botones de apuesta
        betButtonPlus1.setOnClickListener(view -> increaseBet(1));
        betButtonPlus10.setOnClickListener(view -> increaseBet(10));
        betButtonPlus100.setOnClickListener(view -> increaseBet(100));

        // Referencias a los botones
        Button redButton = findViewById(R.id.red_button);
        Button greenButton = findViewById(R.id.green_button);
        Button blackButton = findViewById(R.id.black_button);
        Button numButton = findViewById(R.id.num_button);
        // Configurar el listener para el botón de colocar apuesta
        placeBetButton.setOnClickListener(view -> placeBet());
        //Configurar listener del botón para seleccionar numero
        numButton.setOnClickListener(v -> mostrarSelectorNumero());
        // Configurar el listener del botón de girar
        btnGirar.setOnClickListener(view -> girarRuleta());
        // Configuración de los listeners
        redButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedColor = "rojo";  // Guardar el color apostado
                apuestaTextView.setText("Se ha apostado al color rojo");
            }
        });

        greenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedColor = "verde";
                apuestaTextView.setText("Se ha apostado al color verde");
            }
        });

        blackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedColor = "negro";
                apuestaTextView.setText("Se ha apostado al color negro");
            }
        });
    }


    // Método para aumentar la apuesta
    private void increaseBet(int amount) {
        // Verificar si el usuario tiene suficiente saldo
        if (currentBalance >= amount) {
            // Restar del saldo
            currentBalance -= amount;
            // Aumentar la apuesta
            currentBetAmount += amount;

            // Actualizar las vistas
            balanceValue.setText(String.valueOf(currentBalance));
            updateBetAmountText();
        } else {
            // Mostrar mensaje si no hay suficiente saldo
            Toast.makeText(this, "No tienes suficiente saldo", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para actualizar el texto de la cantidad de apuesta
    private void updateBetAmountText() {
        if (currentBetAmount > 0) {
            betAmount.setText("Bet Amount: " + currentBetAmount);
        } else {
            betAmount.setText("Bet Amount: minimum 1");
        }
    }

    // Método para colocar la apuesta
    private void placeBet() {
        if (currentBetAmount > 0) {
            // Aquí iría la lógica para procesar la apuesta
            // Por ahora, simplemente mostramos un mensaje
            Toast.makeText(this, "Apuesta de " + currentBetAmount + " colocada", Toast.LENGTH_SHORT).show();

            // También podríamos habilitar el botón de girar la ruleta
            // y deshabilitar los botones de apuesta hasta que termine la ronda
        } else {
            Toast.makeText(this, "Debes hacer una apuesta primero", Toast.LENGTH_SHORT).show();
        }
    }

    private void girarRuleta() {
        // Verificar si hay una apuesta hecha
        if (currentBetAmount <= 0) {
            Toast.makeText(this, "Debes hacer una apuesta primero", Toast.LENGTH_SHORT).show();
            return;
        }

        // Reproducir el sonido cuando se gira la ruleta
        reproducirSonido();

        Random random = new Random();
        int vueltas = 5; // Cantidad de vueltas completas
        int angle = random.nextInt(360) + (360 * vueltas); // Ángulo final

        // Crear la animación con inicio rápido y desaceleración progresiva
        RotateAnimation rotateAnimation = new RotateAnimation(0, angle,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        rotateAnimation.setDuration(10000); // Cambiado a 10 segundos para que coincida con el sonido
        rotateAnimation.setFillAfter(true); // Mantener la posición final
        rotateAnimation.setInterpolator(new AccelerateDecelerateInterpolator()); // Aceleración al inicio y desaceleración progresiva

        // Establecer un listener para cuando la animación termine
        rotateAnimation.setAnimationListener(new android.view.animation.Animation.AnimationListener() {
            @Override
            public void onAnimationStart(android.view.animation.Animation animation) {
                // Deshabilitar botones durante la animación
                betButtonPlus1.setEnabled(false);
                betButtonPlus10.setEnabled(false);
                betButtonPlus100.setEnabled(false);
                placeBetButton.setEnabled(false);
                btnGirar.setEnabled(false);
            }

            @Override
            public void onAnimationEnd(android.view.animation.Animation animation) {
                // Calcular la casilla final después de que termine la animación
                calcularCasilla(angle);
                // Verificar si el color apostado coincide con el color resultado
                if (selectedColor.equals(resColor)) {
                    if (resColor.equals("verde")) {
                        // Si el color es verde, multiplica la apuesta por 14
                        currentBalance += currentBetAmount * 14;
                        balanceValue.setText(String.valueOf(currentBalance));
                        apuestaTextView.setText("¡Ganaste! Saldo actualizado. Multiplicaste por 14.");
                    } else {
                        // Si el color es rojo o negro, suma la apuesta al saldo
                        currentBalance += currentBetAmount * 2;
                        balanceValue.setText(String.valueOf(currentBalance));
                        apuestaTextView.setText("¡Ganaste! Saldo actualizado.");
                    }
                } else {
                    apuestaTextView.setText("Perdiste. Inténtalo de nuevo.");
                }

                // Habilitar botones después de la animación
                betButtonPlus1.setEnabled(true);
                betButtonPlus10.setEnabled(true);
                betButtonPlus100.setEnabled(true);
                placeBetButton.setEnabled(true);
                btnGirar.setEnabled(true);

                // Reiniciar la apuesta actual
                currentBetAmount = 0;
                updateBetAmountText();
            }

            @Override
            public void onAnimationRepeat(android.view.animation.Animation animation) {
                // No necesitamos hacer nada cuando la animación se repite
            }
        });

        // Iniciar la animación de la ruleta
        ruletaImage.startAnimation(rotateAnimation);
    }

    // Método para reproducir el sonido
    private void reproducirSonido() {
        // Detener y liberar reproductor anterior si existe
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }

        // Crear y reproducir el nuevo sonido
        mediaPlayer = MediaPlayer.create(this, R.raw.efecto);
        mediaPlayer.start();
    }

    private void calcularCasilla(int angle) {
        // Normalizar el ángulo a un valor entre 0-360
        int normalizedAngle = angle % 360;

        // La ruleta tiene 37 casillas
        int numCasillas = casillasRuleta.length;

        // Cada casilla tiene un ángulo de 360° / 37
        double anguloPorCasilla = 360.0 / numCasillas;

        // Ajustar el ángulo para tener en cuenta la posición de inicio
        int offsetAngulo = 0; // Ajusta este valor según sea necesario

        // Calcular el índice con ajuste de dirección
        int indice = numCasillas - 1 - (int) (((normalizedAngle + offsetAngulo) % 360) / anguloPorCasilla);

        // Ajustar por el desplazamiento de una posición
        indice = (indice + 1) % numCasillas;

        // Asegurar que el índice esté dentro del rango
        indice = (indice + numCasillas) % numCasillas;

        // Obtener el número de la casilla
        String casillaFinal = casillasRuleta[indice];

        // Determinar el color del número
        int colorNumero;
        if (casillaFinal.equals("0")) {
            resColor = "verde";
            colorNumero = Color.GREEN;
        } else if (numerosRojos.contains(casillaFinal)) {
            resColor = "rojo";
            colorNumero = Color.RED;
        } else if (numerosNegros.contains(casillaFinal)) {
            resColor = "negro";
            colorNumero = Color.BLACK;
        } else {
            resColor = "gris";
            colorNumero = Color.GRAY; // Por si hay algún error
        }

        // Actualizar el TextView con el número final y su color
        textViewNumero.setText("Número: " + casillaFinal);
        // Actualizar el CirculosView con el color correspondiente
        circleView.setCircleColor(colorNumero);

        // Para depuración
        Log.d("Ruleta", "Ángulo: " + normalizedAngle + ", Índice: " + indice +
                ", Número: " + casillaFinal);
    }

    @Override
    protected void onDestroy() {
        // Liberar recursos al destruir la actividad
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
    private void mostrarSelectorNumero() {
        // Crear un array con los números del 0 al 36
        String[] numeros = new String[37];
        for (int i = 0; i <= 36; i++) {
            numeros[i] = String.valueOf(i);
        }

        // Crear y mostrar el diálogo de selección
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona un número")
                .setItems(numeros, (dialog, which) -> {
                    // Guardar el número seleccionado en la variable de la clase
                    numeroSeleccionado = Integer.parseInt(numeros[which]);
                    Toast.makeText(this, "Número seleccionado: " + numeroSeleccionado, Toast.LENGTH_SHORT).show();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}