package com.example.ludopatics;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private List<Apuesta> listaApuestas = new ArrayList<>();
    String casillaFinal ="";
    private String selectedColor = ""; // Variable para almacenar el color apostado
    private String resColor = "";
    private ImageView ruletaImage;
    private TextView apuestaTextView;
    private Button btnGirar;
    private TextView textViewNumero;
    private CirculosView circleView;
    private CirculosView circulo1;
    private CirculosView circulo2;
    private CirculosView circulo3;
    private MediaPlayer mediaPlayer;

    // Variables para manejar las apuestas
    private TextView balanceValue;
    private TextView betAmount;

    private TextView apuestaNumeroTextView;
    private TextView apuestaParImparTextView;
    private Button betButtonPlus1;
    private Button betButtonPlus10;
    private Button betButtonPlus100;
    private Button placeBetButton;
    private Button btnodd;
    private Button btneven;
    private Button num_Button;
    private TextView roundTextView;
    private int roundCount = 1;

    // Variables para el seguimiento de la apuesta y el saldo
    private int currentBalance = 1000; // Saldo inicial
    private int currentBetAmount = 0; // Cantidad de apuesta actual
    private String numeroSeleccionado = "";
    private String selectedParImpar = "null";
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
        TextView tvUsername = findViewById(R.id.username); // Obtener el TextView
        // Obtener el nombre de usuario desde el Intent
        String nombreUsuario = getIntent().getStringExtra("nombreUsuario");

        // Verificar si el nombre no es nulo ni vacío antes de mostrarlo
        if (nombreUsuario != null && !nombreUsuario.isEmpty()) {
            tvUsername.setText(nombreUsuario);
        }
        // Inicializar las vistas
        ruletaImage = findViewById(R.id.ruletaImage);
        btnGirar = findViewById(R.id.btnGirar);
        textViewNumero = findViewById(R.id.textViewNumero);
        circleView = findViewById(R.id.circleView);
        circulo1 = findViewById(R.id.circulo1);
        circulo2 = findViewById(R.id.circulo2);
        circulo3 = findViewById(R.id.circulo3);
        apuestaTextView = findViewById(R.id.apuesta);
        apuestaNumeroTextView = findViewById(R.id.apuestaNumero);
        apuestaParImparTextView = findViewById(R.id.apuestaParImpar);
        // Inicializar las vistas relacionadas con las apuestas
        balanceValue = findViewById(R.id.balanceValue);
        betAmount = findViewById(R.id.bet_amount);
        betButtonPlus1 = findViewById(R.id.bet_button_plus);
        betButtonPlus10 = findViewById(R.id.bet_button_plus10);
        betButtonPlus100 = findViewById(R.id.bet_button_plus100);
        placeBetButton = findViewById(R.id.place_bet_button);
        roundTextView = findViewById(R.id.Round);
        // Configurar el saldo inicial
        balanceValue.setText(String.valueOf(currentBalance));

        // Configurar el texto inicial de la apuesta
        updateBetAmountText();

        // Configurar los listeners para los botones de apuesta
        betButtonPlus1.setOnClickListener(view -> increaseBet(1));
        betButtonPlus10.setOnClickListener(view -> increaseBet(10));
        betButtonPlus100.setOnClickListener(view -> increaseBet(100));

        // Referencias a los botones
        btnodd = findViewById(R.id.odd_button);
        btneven=  findViewById(R.id.even_button);
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
        redButton.setOnClickListener(v -> {
            agregarApuesta("color", "rojo");
            selectedColor = "rojo"; // Actualizar el color seleccionado
        });

        blackButton.setOnClickListener(v -> {
            agregarApuesta("color", "negro");
            selectedColor = "negro"; // Actualizar el color seleccionado
        });

        greenButton.setOnClickListener(v -> {
            agregarApuesta("color", "verde");
            selectedColor = "verde"; // Actualizar el color seleccionado
        });

        btnodd.setOnClickListener(v -> {
            agregarApuesta("parImpar", "par");  // Make sure the parameter count is consistent
            selectedParImpar = "par"; // Actualizar la selección de par/impar
        });
        btneven.setOnClickListener(v -> {
            agregarApuesta("parImpar", "impar");  // Make sure the parameter count is consistent
            selectedParImpar = "impar"; // Actualizar la selección de par/impar
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
                Toast.makeText(this, "Apuesta de " + currentBetAmount + " colocada", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Debes hacer una apuesta primero", Toast.LENGTH_SHORT).show();
        }
        // Reiniciar monto de apuesta
        currentBetAmount = 0;
        updateBetAmountText();
    }

    private void girarRuleta() {
        if (currentBetAmount <= 0) {
            Toast.makeText(this, "Debes hacer una apuesta primero", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar si al menos una de las opciones ha sido seleccionada
        if (selectedColor.equals("") && selectedParImpar.equals("") && numeroSeleccionado.equals("")) {
            Toast.makeText(this, "Debes hacer una apuesta a color, número o Par/Impar", Toast.LENGTH_SHORT).show();
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
                roundCount++;
                roundTextView.setText("Round #" + roundCount);
                // Mostrar si has ganado o perdido
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
                // Mover los colores hacia la izquierda
                circulo2.setCircleColor(circulo3.getCircleColor());
                circulo1.setCircleColor(circulo2.getCircleColor());

                if (resColor.equals("rojo")){circulo3.setCircleColor(Color.RED);}
                if (resColor.equals("negro")){circulo3.setCircleColor(Color.BLACK);}
                if (resColor.equals("verde")){circulo3.setCircleColor(Color.GREEN);}

                   // Verificar la apuesta al número
                if (!numeroSeleccionado.equals("") && numeroSeleccionado.equals(casillaFinal)) {

                    // Si el número apostado coincide con el número resultado
                    currentBalance += currentBetAmount * 35; // La ruleta paga 35 veces la apuesta por el número
                    balanceValue.setText(String.valueOf(currentBalance));
                    apuestaTextView.append("\n¡Ganaste! Apuesta al número correcta. Multiplicaste por 35.");
                } else if (!numeroSeleccionado.equals("")) {
                    // Si el número apostado no coincide
                    apuestaTextView.append("\nPerdiste en el número. Inténtalo de nuevo.");
                }
                // Verificar si apostó a par o impar
                if (!selectedParImpar.equals("")) { // Verifica si ha apostado a par o impar
                    int resultadoNumero = Integer.parseInt(casillaFinal); // Convierte el resultado en número
                    boolean esPar = resultadoNumero % 2 == 0;

                    if ((selectedParImpar.equals("par") && esPar) || (selectedParImpar.equals("impar") && !esPar)) {
                        // Si la apuesta coincide con el resultado, gana el doble
                        currentBalance += currentBetAmount * 2;
                        balanceValue.setText(String.valueOf(currentBalance));
                        apuestaTextView.append("\n¡Ganaste! Apuesta a " + selectedParImpar + " correcta. Multiplicaste por 2.");
                    } else {
                        // Si la apuesta no coincide, pierde la apuesta
                        balanceValue.setText(String.valueOf(currentBalance));
                        apuestaTextView.append("\nPerdiste en " + selectedParImpar + ". Inténtalo de nuevo.");
                    }
                }
                // Mostrar los círculos gradualmente según el número de tiradas
                if (roundCount == 1) {
                    circulo1.setVisibility(View.VISIBLE); // Muestra el primer círculo
                    circulo1.setText(casillaFinal);
                }

                if (roundCount == 2) {
                    circulo2.setVisibility(View.VISIBLE); // Muestra el segundo círculo

                }

                if (roundCount >= 3) {
                    circulo3.setVisibility(View.VISIBLE); // Muestra el tercer círculo
                    }




                // Usamos un Handler para hacer una pausa antes de habilitar los botones y resetear los TextViews
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Habilitar botones después de la animación
                        betButtonPlus1.setEnabled(true);
                        betButtonPlus10.setEnabled(true);
                        betButtonPlus100.setEnabled(true);
                        placeBetButton.setEnabled(true);
                        btnGirar.setEnabled(true);

                        // Reiniciar la apuesta actual
                        currentBetAmount = 0;
                        updateBetAmountText();

                        // Restablecer los TextViews a su contenido inicial
                        apuestaTextView.setText("Apuesta: Ninguna");
                        apuestaNumeroTextView.setText("Apuesta al número: Ninguna");
                        apuestaParImparTextView.setText("Apuesta Par/Impar: Ninguna");
                    }
                }, 3000);
                selectedColor="null";// Pausa de 2 segundos (2000 milisegundos) antes de habilitar los botones y resetear los TextViews
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
        casillaFinal = casillasRuleta[indice];

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
        textViewNumero.setText(casillaFinal);
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
                    numeroSeleccionado = numeros[which];
                    // Actualizar el TextView con la apuesta del número
                    apuestaNumeroTextView.setText("Apostaste " + currentBetAmount + " al " + numeroSeleccionado);
                    Toast.makeText(this, "Número seleccionado: " + numeroSeleccionado, Toast.LENGTH_SHORT).show();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void agregarApuesta(String tipo, String valor) {
        if (currentBetAmount <= 0) {
            Toast.makeText(this, "Primero selecciona un monto de apuesta", Toast.LENGTH_SHORT).show();
            return;
        }

        // Eliminar apuesta anterior del mismo tipo (si existe)
        Iterator<Apuesta> iterator = listaApuestas.iterator();
        while (iterator.hasNext()) {
            Apuesta apuesta = iterator.next();
            if (apuesta.tipo.equals(tipo)) {
                iterator.remove();
            }
        }

        // Agregar la nueva apuesta
        listaApuestas.add(new Apuesta(tipo, valor, currentBetAmount));

        // Mostrar en pantalla
        // Mostrar en el TextView correspondiente según el tipo de apuesta
        switch (tipo) {
            case "color":
                apuestaTextView.setText("Apostaste " + currentBetAmount + " al color " + valor);
                break;
            case "numero":
                apuestaNumeroTextView.setText("Apostaste " + currentBetAmount + " al número " + valor);
                break;
            case "parImpar":
                apuestaParImparTextView.setText("Apostaste " + currentBetAmount + " a " + valor);
                break;
            default:
                // En caso de error o tipo desconocido
                Log.e("Apuesta", "Tipo de apuesta desconocido: " + tipo);
        }


    }

}
