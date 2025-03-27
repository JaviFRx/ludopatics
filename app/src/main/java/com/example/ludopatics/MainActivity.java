package com.example.ludopatics;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import androidx.appcompat.app.ActionBar;
public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    private final List<Apuesta> listaApuestas = new ArrayList<>();
    String casillaFinal = "";
    int totalApuesta = 0;
    private String selectedColor = ""; // Variable para almacenar el color apostado
    private String resColor = "";
    private ImageView ruletaImage;
    private TextView apuestaTextView;
    private Button btnGirar;

    private CirculosView circleView, circulo1, circulo2, circulo3, circulo4, circulo5;

    private MediaPlayer mediaPlayer;
    public static MediaPlayer mediaPlayer2;

    // Variables para manejar las apuestas
    private TextView balanceValue;
    private TextView betAmount;
    private GestureDetector gestureDetector;
    private boolean isActionBarVisible = false;
    private Handler hideHandler = new Handler();

    private TextView apuestaNumeroTextView;
    private TextView apuestaParImparTextView;
    private Button betButtonPlus1;
    private Button betButtonPlus10;
    private Button betButtonPlus100;
    private Button btnodd;
    private Button btneven;
    private Button num_Button;
    private TextView roundTextView;
    private int roundCount = 0;

    // Variables para el seguimiento de la apuesta y el saldo
    private int currentBalance = 1000; // Saldo inicial
    private int currentBetAmount = 0; // Cantidad de apuesta actual
    private String nombreUsuario = "";
    private final String numeroSeleccionado = "";
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
        dbHelper = new DatabaseHelper(this);
        // Obtiene la ActionBar
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setLogo(R.mipmap.ic_launcher);

            // Configurar el título
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle("Ludopatics");
            actionBar.hide();

        }
        final boolean[] isActionBarVisible = {false};

        // Configurar el botón de imagen
        ImageButton menuButton = findViewById(R.id.btn_toggle_actionbar);
        menuButton.setOnClickListener(view -> {
            if (actionBar != null) {
                if (isActionBarVisible[0]) {
                    actionBar.hide();
                } else {
                    actionBar.show();
                }
                isActionBarVisible[0] = !isActionBarVisible[0];
                Log.d("ActionBarTest", "ActionBar visibility toggled to: " + !isActionBarVisible[0]);
            } else {
                Log.e("ActionBarTest", "ActionBar is null!");
            }
        });


        TextView tvUsername = findViewById(R.id.username); // Obtener el TextView
        // Obtener el nombre de usuario desde el Intent
        nombreUsuario = getIntent().getStringExtra("nombreUsuario");

        // Verificar si el nombre no es nulo ni vacío antes de mostrarlo
        if (nombreUsuario != null && !nombreUsuario.isEmpty()) {
            tvUsername.setText(nombreUsuario);
        }
        // Inicializa el MediaPlayer con el archivo de audio
        mediaPlayer2 = MediaPlayer.create(this, R.raw.jazz);

        // Inicia la reproducción del audio
        mediaPlayer2.start();

        // Asegúrate de liberar los recursos al finalizar
        mediaPlayer2.setOnCompletionListener(mp -> {
            mediaPlayer2.release();
            mediaPlayer2 = null;
        });
        // Inicializar las vistas
        ruletaImage = findViewById(R.id.ruletaImage);
        btnGirar = findViewById(R.id.btnGirar);
        circleView = findViewById(R.id.circleView);
        circulo1 = findViewById(R.id.circulo1);
        circulo2 = findViewById(R.id.circulo2);
        circulo3 = findViewById(R.id.circulo3);
        circulo4 = findViewById(R.id.circulo4);
        circulo5 = findViewById(R.id.circulo5);
        apuestaTextView = findViewById(R.id.apuesta);
        apuestaNumeroTextView = findViewById(R.id.apuestaNumero);
        apuestaParImparTextView = findViewById(R.id.apuestaParImpar);
        // Inicializar las vistas relacionadas con las apuestas
        balanceValue = findViewById(R.id.balanceValue);
        betAmount = findViewById(R.id.bet_amount);
        betButtonPlus1 = findViewById(R.id.bet_button_plus);
        betButtonPlus10 = findViewById(R.id.bet_button_plus10);
        betButtonPlus100 = findViewById(R.id.bet_button_plus100);

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
        btneven = findViewById(R.id.even_button);
        Button redButton = findViewById(R.id.red_button);
        Button greenButton = findViewById(R.id.green_button);
        Button blackButton = findViewById(R.id.black_button);
        Button numButton = findViewById(R.id.num_button);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflar el archivo menu.xml para que aparezcan los íconos en la ActionBar
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.music_toggle) {  // Verifica si es el botón de música
            if (mediaPlayer2.isPlaying()) {
                // Detener la música si está reproduciéndose
                mediaPlayer2.pause();
                item.setIcon(R.drawable.speaker);  // Cambiar el ícono a uno de apagado
                Toast.makeText(this, "Música detenida", Toast.LENGTH_SHORT).show();
            } else {
                // Reanudar la música si está detenida
                mediaPlayer2.start();
                item.setIcon(R.drawable.speakeroff);  // Cambiar el ícono a uno de encendido
                Toast.makeText(this, "Música iniciada", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // Método para aumentar la apuesta
    private void increaseBet(int amount) {
        // Verificar si el usuario tiene suficiente saldo
        if (currentBalance >= amount) {
            // Solo aumentar la apuesta (NO restar del saldo aún)
            currentBetAmount += amount;

            // Actualizar la vista del monto de apuesta
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
        if (currentBetAmount <= 0) {
            Toast.makeText(this, "Debes ingresar una cantidad para apostar", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar que el usuario haya seleccionado al menos una opción
        if (selectedColor.isEmpty() && selectedParImpar.isEmpty() && numeroSeleccionado.isEmpty()) {
            Toast.makeText(this, "Debes seleccionar una opción: Color, Número o Par/Impar", Toast.LENGTH_SHORT).show();
            return;
        }
    }


    private void girarRuleta() {
        if (listaApuestas.isEmpty()) {
            Toast.makeText(this, "Debes hacer una apuesta primero", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar si al menos una de las opciones ha sido seleccionada
        if (selectedColor.isEmpty() && selectedParImpar.isEmpty() && numeroSeleccionado.isEmpty()) {
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

                int totalApostado = calcularTotalApuesta();

                // Deshabilitar botones durante la animación
                betButtonPlus1.setEnabled(false);
                betButtonPlus10.setEnabled(false);
                betButtonPlus100.setEnabled(false);
                btnGirar.setEnabled(false);

            }

            @Override
            public void onAnimationEnd(android.view.animation.Animation animation) {
                // Calcular la casilla final después de que termine la animación

                calcularCasilla(angle);
                String color = resColor;
                roundCount++;
                roundTextView.setText("Round #" + roundCount);

                comprobarApuestas(resColor, casillaFinal);
                // Mover los colores hacia la izquierda
                circulo1.setCircleColor(circulo2.getCircleColor());
                circulo2.setCircleColor(circulo3.getCircleColor());
                circulo3.setCircleColor(circulo4.getCircleColor());
                circulo4.setCircleColor(circulo5.getCircleColor());

                // Mover el texto hacia la izquierda
                circulo1.setText(circulo2.getText());
                circulo2.setText(circulo3.getText());
                circulo3.setText(circulo4.getText());
                circulo4.setText(circulo5.getText());

                // Asignar color y texto al nuevo círculo (circulo5)
                switch (color) {
                    case "rojo":
                        circulo5.setCircleColor(Color.RED);
                        break;
                    case "negro":
                        circulo5.setCircleColor(Color.BLACK);
                        break;
                    case "verde":
                        circulo5.setCircleColor(Color.GREEN);
                        break;
                }
                circulo5.setText(casillaFinal);

                // Mostrar los círculos gradualmente según el número de tiradas
                if (roundCount == 1) {
                    circulo5.setVisibility(View.VISIBLE); // Muestra el primer círculo
                } else if (roundCount == 2) {
                    circulo4.setVisibility(View.VISIBLE); // Muestra el segundo círculo
                } else if (roundCount == 3) {
                    circulo3.setVisibility(View.VISIBLE); // Muestra el tercero
                } else if (roundCount == 4) {
                    circulo2.setVisibility(View.VISIBLE); // Muestra el cuarto
                } else if (roundCount >= 5) {
                    circulo1.setVisibility(View.VISIBLE); // Muestra el quinto círculo
                }


                // Usamos un Handler para hacer una pausa antes de habilitar los botones y resetear los TextViews
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Comprobar si ya hemos alcanzado 10 rondas o si el jugador se ha quedado sin dinero
                        if (roundCount >= 10 || currentBalance <= 0) {
                            finalizarJuego();  // Llamamos al método para finalizar el juego
                            return;  // Salimos de la función si el juego ya ha terminado
                        }
                        // Habilitar botones después de la animación
                        betButtonPlus1.setEnabled(true);
                        betButtonPlus10.setEnabled(true);
                        betButtonPlus100.setEnabled(true);
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
                selectedColor = "null";// Pausa de 2 segundos (2000 milisegundos) antes de habilitar los botones y resetear los TextViews
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
        // Pausa el audio de jazz si está reproduciéndose
        if (mediaPlayer2 != null && mediaPlayer2.isPlaying()) {
            mediaPlayer2.pause();
        }

        // Detener y liberar el reproductor de sonidos de la ruleta si existe
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }

        // Crear y reproducir el nuevo sonido (ruleta)
        mediaPlayer = MediaPlayer.create(this, R.raw.efecto);
        mediaPlayer.start();

        // Listener para retomar el audio de jazz después de que el sonido de la ruleta termine
        mediaPlayer.setOnCompletionListener(mp -> {
            mediaPlayer.release();
            mediaPlayer = null;

            // Retomar el audio de jazz si no fue liberado
            if (mediaPlayer2 != null) {
                mediaPlayer2.start();
            }
        });
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

        // Actualizar el CirculosView con el color correspondiente
        circleView.setCircleColor(colorNumero);
        circleView.setText(casillaFinal);

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
        // Crear un NumberPicker
        NumberPicker numberPicker = new NumberPicker(this);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(36);
        numberPicker.setWrapSelectorWheel(true);

        // Cambiar el color del texto del NumberPicker a blanco
        setNumberPickerTextColor(numberPicker, Color.WHITE);

        // Crear el AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona un número")
                .setView(numberPicker)
                .setPositiveButton("Seleccionar", (dialog, which) -> {
                    int numeroSeleccionado = numberPicker.getValue();
                    apuestaNumeroTextView.setText("Apostaste " + currentBetAmount + " al " + numeroSeleccionado);
                    Toast.makeText(this, "Número seleccionado: " + numeroSeleccionado, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null);

        // Crear y mostrar el diálogo
        AlertDialog dialog = builder.create();

        // Aplicar fondo personalizado y cambiar color de los botones después de que se muestre
        dialog.setOnShowListener(d -> {
            // Cambiar el fondo del AlertDialog
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.background_gradientvertical);

            // Cambiar color del título y botones a blanco
            TextView titleText = dialog.findViewById(android.R.id.title);
            if (titleText != null) {
                titleText.setTextColor(Color.WHITE);  // Título en blanco
            }

            Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            if (positiveButton != null) {
                positiveButton.setTextColor(Color.WHITE);  // Botón positivo en blanco
            }

            Button negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            if (negativeButton != null) {
                negativeButton.setTextColor(Color.WHITE);  // Botón negativo en blanco
            }
        });

        dialog.show();
    }

    /**
     * Método para cambiar el color del texto en el NumberPicker sin usar reflexión.
     */
    private void setNumberPickerTextColor(NumberPicker numberPicker, int color) {
        for (int i = 0; i < numberPicker.getChildCount(); i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof EditText) {
                ((EditText) child).setTextColor(color); // Cambia el color del texto a blanco
                ((EditText) child).setTypeface(Typeface.DEFAULT_BOLD); // Hace el texto más llamativo
            }
        }
    }


    private void agregarApuesta(String tipo, String valor) {
        if (currentBetAmount <= 0) {
            Toast.makeText(this, "Primero selecciona un monto de apuesta", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean replacingExistingBet = false;
        // Buscar si ya existe una apuesta del mismo tipo y eliminarla de forma segura
        Iterator<Apuesta> iterator = listaApuestas.iterator();
        while (iterator.hasNext()) {
            Apuesta apuesta = iterator.next();
            if (apuesta.tipo.equals(tipo)) {
                currentBalance += apuesta.monto; // Reembolsar la apuesta anterior
                iterator.remove();  // Eliminar la apuesta anterior de forma segura
                replacingExistingBet = true;
                break;  // Salimos ya que solo puede haber una apuesta del mismo tipo
            }
        }

        // Actualizar UI para mostrar que se reembolsó una apuesta (opcional)
        if (replacingExistingBet) {
            actualizarSaldoUI(); // Actualizar saldo después del reembolso
        }

        // Restar la nueva apuesta del saldo
        if (currentBalance >= currentBetAmount) {
            currentBalance -= currentBetAmount;
        } else {
            Toast.makeText(this, "No tienes suficiente saldo para esta apuesta", Toast.LENGTH_SHORT).show();
            return; // Salir sin registrar la apuesta
        }

        // Agregar la nueva apuesta
        listaApuestas.add(new Apuesta(tipo, valor, currentBetAmount));

        // Actualizar UI
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
                Log.e("Apuesta", "Tipo de apuesta desconocido: " + tipo);
        }

        actualizarSaldoUI(); // Asegurar que la interfaz muestra el saldo correcto
        currentBetAmount = 0; // Reiniciar el monto apostado *después* de actualizar la UI
    }

    private void actualizarSaldoUI() {
        balanceValue.setText(String.valueOf(currentBalance));

    }

    /**
     * Comprueba todas las apuestas y actualiza el saldo según los resultados
     *
     * @param resColor     El color resultante de la ruleta (rojo, negro o verde)
     * @param casillaFinal El número resultante de la ruleta
     */
    private void comprobarApuestas(String resColor, String casillaFinal) {
        StringBuilder mensajeResultado = new StringBuilder();
        boolean ganoAlgo = false;

        // Comprobar apuesta al color
        if (selectedColor.equals(resColor)) {
            ganoAlgo = true;
            if (resColor.equals("verde")) {
                // Si el color es verde, multiplica la apuesta por 14
                currentBalance += currentBetAmount * 14;
                mensajeResultado.append("¡Ganaste! Color verde. Multiplicaste por 14.");
            } else {
                // Si el color es rojo o negro, multiplica la apuesta por 2
                currentBalance += currentBetAmount * 2;
                mensajeResultado.append("¡Ganaste! Color ").append(resColor).append(". Multiplicaste por 2.");
            }
        } else if (!selectedColor.equals("null")) {
            mensajeResultado.append("Perdiste en color. ");
        }

        // Comprobar apuesta al número
        if (!numeroSeleccionado.isEmpty() && numeroSeleccionado.equals(casillaFinal)) {
            ganoAlgo = true;
            // Si el número apostado coincide con el número resultado
            currentBalance += currentBetAmount * 35; // La ruleta paga 35 veces la apuesta por el número
            if (mensajeResultado.length() > 0) {
                mensajeResultado.append("\n");
            }
            mensajeResultado.append("¡Ganaste! Número ").append(casillaFinal).append(". Multiplicaste por 35.");
        } else if (!numeroSeleccionado.isEmpty()) {
            if (mensajeResultado.length() > 0) {
                mensajeResultado.append("\n");
            }
            mensajeResultado.append("Perdiste en número. ");
        }

        // Comprobar apuesta a par/impar
        if (!selectedParImpar.isEmpty()) {
            int resultadoNumero = Integer.parseInt(casillaFinal);
            boolean esPar = resultadoNumero % 2 == 0;

            if ((selectedParImpar.equals("par") && esPar) || (selectedParImpar.equals("impar") && !esPar)) {
                ganoAlgo = true;
                // Si la apuesta coincide con el resultado, gana el doble
                currentBalance += currentBetAmount * 2;
                if (mensajeResultado.length() > 0) {
                    mensajeResultado.append("\n");
                }
                mensajeResultado.append("¡Ganaste! Apuesta a ").append(selectedParImpar).append(". Multiplicaste por 2.");
            } else {
                if (mensajeResultado.length() > 0) {
                    mensajeResultado.append("\n");
                }
                mensajeResultado.append("Perdiste en ").append(selectedParImpar).append(".");
            }
        }

        // Si no ganó nada y no hay mensajes, mostrar mensaje genérico
        if (!ganoAlgo && mensajeResultado.length() == 0) {
            mensajeResultado.append("Perdiste. Inténtalo de nuevo.");
        }

        // Actualizar la UI
        balanceValue.setText(String.valueOf(currentBalance));
        apuestaTextView.setText(mensajeResultado.toString());
    }

    public int calcularTotalApuesta() {
        int totalApuesta = 0;
        for (Apuesta apuesta : listaApuestas) {
            totalApuesta += apuesta.monto;
        }
        return totalApuesta;
    }

    private void finalizarJuego() {
        // Aquí puedes mostrar un mensaje al jugador o realizar otras acciones
        if (roundCount >= 10) {
            // Si se han alcanzado las 10 rondas
            Toast.makeText(this, "¡Juego terminado! Has alcanzado el límite de rondas.", Toast.LENGTH_SHORT).show();
        } else if (currentBalance <= 0) {
            // Si se ha quedado sin dinero
            Toast.makeText(this, "¡Juego terminado! Te has quedado sin dinero.", Toast.LENGTH_SHORT).show();
        }


        // Usar el método obtenerIdJugador para obtener el ID del jugador desde la base de datos
        int usuarioId = dbHelper.obtenerIdJugador(nombreUsuario);

        if (usuarioId != -1) {
            // Guardar la partida en la base de datos
            long idPartida = dbHelper.crearPartida(usuarioId, currentBalance); // Usar currentBalance como saldo final
            Log.d("Juego", "Partida guardada con ID: " + idPartida);
        } else {
            Log.e("Juego", "Error: No se encontró el ID para el jugador con nombre: " + nombreUsuario);
        }

        // Pasar los datos a la pantalla GameOverActivity
        Intent intent = new Intent(this, GameOverActivity.class);
        intent.putExtra("roundCount", roundCount);
        intent.putExtra("currentBalance", currentBalance);
        intent.putExtra("nombreUsuario", nombreUsuario);
        startActivity(intent);

        // Finalizar esta actividad
        finish();

        // Deshabilitar botones para evitar más apuestas o giros
        betButtonPlus1.setEnabled(false);
        betButtonPlus10.setEnabled(false);
        betButtonPlus100.setEnabled(false);
        btnGirar.setEnabled(false);
    }
}
