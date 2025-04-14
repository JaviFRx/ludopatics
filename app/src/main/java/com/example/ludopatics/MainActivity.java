package com.example.ludopatics;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
/** @noinspection SpellCheckingInspection*/
public class MainActivity extends AppCompatActivity {

    private boolean musicaActivada = true; // Indica si la música está activada o no

    private DatabaseHelper dbHelper;

    private final List<Apuesta> listaApuestas = new ArrayList<>();
    String casillaFinal = "";
    private String selectedColor = ""; // Almacena el color al que se apuesta
    private String resColor = "";
    private ImageView ruletaImage;
    private TextView apuestaTextView;
    private Button btnGirar;


    private CirculosView circleView, circulo1, circulo2, circulo3, circulo4, circulo5;

    private MediaPlayer mediaPlayer;
    public static MediaPlayer mediaPlayer2;

    private Uri audioUri;

    // Variables para manejar las apuestas
    private TextView balanceValue;
    private TextView betAmount;
    private TextView apuestaNumeroTextView;
    private TextView apuestaParImparTextView;
    private Button betButtonPlus1;
    private Button betButtonPlus10;
    private Button betButtonPlus100;
    private Button btnpar;
    private Button btnimpar;
    private TextView roundTextView;
    private int roundCount = 0;

    // Variables para el seguimiento de la apuesta y el saldo
    private int currentBalance = 1000; // Saldo inicial
    private int currentBetAmount = 0; // Cantidad de apuesta actual
    private String nombreUsuario = "";
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

        dbHelper = new DatabaseHelper(this);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayUseLogoEnabled(true);

            // Restaurar el ícono sin cambios
            actionBar.setLogo(R.mipmap.ic_launcher);

            // Restaurar el título original
            actionBar.setTitle("Ludopatics");

            // Mantener la ActionBar oculta hasta que se pulse el botón
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
        // Recuperar la URI de la música seleccionada desde SharedPreferences
        SharedPreferences prefs = getSharedPreferences("ludopatics", MODE_PRIVATE);
        String uriString = prefs.getString("custom_music_uri", null);
        if (uriString == null) {
            // Inicializa el MediaPlayer con la música por defecto (jazz)
            mediaPlayer2 = MediaPlayer.create(this, R.raw.jazz);
        } else {
            // Si hay música personalizada, usa la URI seleccionada
            audioUri = Uri.parse(uriString);
            mediaPlayer2 = MediaPlayer.create(this, audioUri);
        }

        // Inicia la reproducción del audio
        mediaPlayer2.start();


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
        btnpar = findViewById(R.id.odd_button);
        btnimpar = findViewById(R.id.even_button);
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

        btnpar.setOnClickListener(v -> {
            agregarApuesta("parImpar", "par");
            selectedParImpar = "par"; // Actualizar la selección de par/impar
        });
        btnimpar.setOnClickListener(v -> {
            agregarApuesta("parImpar", "impar");
            selectedParImpar = "impar"; // Actualizar la selección de par/impar
        });


    }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Usar menu.xml para que aparezcan los iconos en la ActionBar
            getMenuInflater().inflate(R.menu.menu, menu);
            return true;
        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Verifica si se ha pulsado el botón de música
        if (item.getItemId() == R.id.music_toggle) {
            if (mediaPlayer2.isPlaying()) {
                // Detener la música si está reproduciéndose
                mediaPlayer2.pause();
                musicaActivada = false;
                item.setIcon(R.drawable.speaker);  // Cambiar el ícono a uno de apagado
                Toast.makeText(this, getString(R.string.music_stopped), Toast.LENGTH_SHORT).show();
            } else {
                // Reanudar la música si está detenida
                mediaPlayer2.start();
                musicaActivada = true;
                item.setIcon(R.drawable.speakeroff);  // Cambiar el ícono a uno de encendido
                Toast.makeText(this, getString(R.string.music_started), Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        // Verifica si se ha pulsado el botón de perfil
        if (item.getItemId() == R.id.action_profile) {
            // Acción para el perfil
            Intent intent = new Intent(this,PerfilActivity.class);
            intent.putExtra("nombreUsuario", nombreUsuario);  // Pasar el nombre de usuario
            startActivity(intent);
            return true;
        }

        // Verifica si se ha pulsado el botón de historial
        if (item.getItemId() == R.id.action_history) {
            Intent intent = new Intent(this, HistorialActivity.class);
            intent.putExtra("nombreUsuario", nombreUsuario);
            startActivity(intent);
            return true;
        }

        // Verifica si se ha pulsado el botón de login
        if (item.getItemId() == R.id.action_login) {
            Intent intent = new Intent(this, Menu.class);
            intent.putExtra("nombreUsuario", nombreUsuario);
            startActivity(intent);
            finish();
            return true;
        }

        // Si no se ha pulsado ninguna de las opciones anteriores
        return super.onOptionsItemSelected(item);
    }




    // Método para aumentar la apuesta
    private void increaseBet(int amount) {
        // Verificar si el monto a apostar no excede el saldo disponible
        if (currentBalance >= currentBetAmount + amount) {
            // Si tiene suficiente saldo, aumentar la apuesta
            currentBetAmount += amount;

            // Actualizar la vista con el nuevo monto de apuesta
            updateBetAmountText();
        } else {
            // Si no hay suficiente saldo, establecer la apuesta al saldo disponible
            currentBetAmount = currentBalance;

            // Mostrar mensaje de que no hay suficiente saldo y que se ajustó la apuesta
            Toast.makeText(this, getString(R.string.insufficient_balance), Toast.LENGTH_SHORT).show();

            // Actualizar la vista con el monto de apuesta ajustado
            updateBetAmountText();
        }
    }



    // Método para actualizar el texto de la cantidad de apuesta
    private void updateBetAmountText() {
        if (currentBetAmount > 0) {
            betAmount.setText(getString(R.string.cantidad_apostada, currentBetAmount));
        } else {
            betAmount.setText(getString(R.string.cantidad_minima));
        }
    }




    private void girarRuleta() {
        if (listaApuestas.isEmpty()) {
            Toast.makeText(this, getString(R.string.bet_first), Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar si al menos una de las opciones ha sido seleccionada
        if (selectedColor.isEmpty() && selectedParImpar.isEmpty() && numeroSeleccionado.isEmpty()) {
            Toast.makeText(this, getString(R.string.bet_on_color_number_parity), Toast.LENGTH_SHORT).show();
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
                btnGirar.setEnabled(false);

            }

            @Override
            public void onAnimationEnd(android.view.animation.Animation animation) {
                // Calcular la casilla final después de que termine la animación

                calcularCasilla(angle);
                String color = resColor;
                roundCount++;
                roundTextView.setText(getString(R.string.ronda_numero, roundCount));
                Log.d("DEBUG", "Antes de comprobarApuestas - currentBetAmount: " + currentBetAmount);
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
                        apuestaTextView.setText(getString(R.string.bet_none));
                        apuestaNumeroTextView.setText(getString(R.string.bet_number_none));
                        apuestaParImparTextView.setText(getString(R.string.bet_even_odd_none));
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

        // Detener y liberar el reproductor de sonidos de la ruleta
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }

        // Crear y reproducir el nuevo sonido de la ruleta
        mediaPlayer = MediaPlayer.create(this, R.raw.efecto);
        mediaPlayer.start();

        // Listener para retomar el audio de jazz después de que el sonido de la ruleta termine
        mediaPlayer.setOnCompletionListener(mp -> {
            mediaPlayer.release();
            mediaPlayer = null;

            // Solo reanudar la música si `musicaActivada` es `true`
            if (musicaActivada && mediaPlayer2 != null) {
                Log.d("MUSICA", "Reanudando música tras ruleta");
                mediaPlayer2.start();
            } else {
                Log.d("MUSICA", "Música desactivada por el usuario, no se reanuda.");
            }
        });
    }

    private void calcularCasilla(int angle) {
        // Normalizar el ángulo a un valor entre 0-360
        int normalizedAngle = angle % 360;

        int indice = getIndice(normalizedAngle);

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
            colorNumero = Color.GRAY;
        }

        // Actualizar el CirculosView con el color correspondiente
        circleView.setCircleColor(colorNumero);
        circleView.setText(casillaFinal);

        // Para depuración
        Log.d("Ruleta", "Ángulo: " + normalizedAngle + ", Índice: " + indice +
                ", Número: " + casillaFinal);
    }

    private int getIndice(int normalizedAngle) {
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
        return indice;
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
                    numeroSeleccionado = String.valueOf(numberPicker.getValue());
                    agregarApuesta("numero", numeroSeleccionado); // REGISTRA LA APUESTA CORRECTAMENTE
                    Toast.makeText(this, getString(R.string.selected_number, numeroSeleccionado), Toast.LENGTH_SHORT).show();

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
     * Método para cambiar el color del texto en el NumberPicker
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
            Toast.makeText(this, getString(R.string.bet_first), Toast.LENGTH_SHORT).show();
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

        // Actualizar UI para mostrar que se reembolsó una apuesta
        if (replacingExistingBet) {
            actualizarSaldoUI(); // Actualizar saldo después del reembolso
        }

        // Restar la nueva apuesta del saldo
        if (currentBalance >= currentBetAmount) {
            currentBalance -= currentBetAmount;
        } else {
            Toast.makeText(this, getString(R.string.no_sufficient_balance_bet), Toast.LENGTH_SHORT).show();
            return; // Salir sin registrar la apuesta
        }

        // Agregar la nueva apuesta
        listaApuestas.add(new Apuesta(tipo, valor, currentBetAmount));

        // Actualizar UI
        switch (tipo) {
            case "color":
                apuestaTextView.setText(getString(R.string.bet_color, 100, "Rojo"));
                break;
            case "numero":
                apuestaNumeroTextView.setText(getString(R.string.bet_number, currentBetAmount, valor));
                break;
            case "parImpar":
                apuestaParImparTextView.setText(getString(R.string.bet_even_odd, currentBetAmount, valor));
                break;
            default:
                }


        actualizarSaldoUI(); // Asegurar que la interfaz muestra el saldo correcto
        currentBetAmount = 0; // Reiniciar el monto apostado despues de actualizar la UI
    }

    private void actualizarSaldoUI() {
        balanceValue.setText(String.valueOf(currentBalance));

        // Si el saldo llega a 0, desactivar botones de apuesta
        boolean haySaldo = currentBalance > 0;
        betButtonPlus1.setEnabled(haySaldo);
        betButtonPlus10.setEnabled(haySaldo);
        betButtonPlus100.setEnabled(haySaldo);
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

        // Recorrer la lista de apuestas
        for (Apuesta apuesta : listaApuestas) {
            switch (apuesta.tipo) {
                case "color":
                    if (apuesta.valor.equals(resColor)) {
                        ganoAlgo = true;
                        int multiplicador = resColor.equals("verde") ? 14 : 2;
                        currentBalance += apuesta.monto * multiplicador;
                        mensajeResultado.append("\n¡Ganaste! Color ").append(resColor).append(". Multiplicaste por ").append(multiplicador).append(".");
                    } else {
                        mensajeResultado.append("\nPerdiste en color.");
                    }
                    break;

                case "numero":
                    if (apuesta.valor.equals(casillaFinal)) {
                        ganoAlgo = true;
                        currentBalance += apuesta.monto * 35;
                        mensajeResultado.append("\n¡Ganaste! Número ").append(casillaFinal).append(". Multiplicaste por 35.");
                    } else {
                        mensajeResultado.append("\nPerdiste en número.");
                    }
                    break;

                case "parImpar":
                    int resultadoNumero = Integer.parseInt(casillaFinal);
                    boolean esPar = resultadoNumero % 2 == 0;
                    if ((apuesta.valor.equals("par") && esPar) || (apuesta.valor.equals("impar") && !esPar)) {
                        ganoAlgo = true;
                        currentBalance += apuesta.monto * 2;
                        mensajeResultado.append("\n¡Ganaste! Apuesta a ").append(apuesta.valor).append(". Multiplicaste por 2.");
                    } else {
                        mensajeResultado.append("\nPerdiste en ").append(apuesta.valor).append(".");
                    }
                    break;
            }
        }

        // Si no ganó nada, mostrar mensaje genérico
        if (!ganoAlgo) {
            mensajeResultado.append("\nPerdiste. Inténtalo de nuevo.");
        }

        // Limpiar la lista de apuestas después de comprobarlas
        listaApuestas.clear();

        // Actualizar la UI
        balanceValue.setText(String.valueOf(currentBalance));
        apuestaTextView.setText(mensajeResultado.toString().trim());
    }




    private void finalizarJuego() {

        if (roundCount >= 10) {
            // Si se han alcanzado las 10 rondas
            Toast.makeText(this, getString(R.string.game_over_rounds_limit), Toast.LENGTH_SHORT).show();
            if (currentBalance >= 1000) {
                // Si su saldo es superior a 1000 significa que ha ganado
                Toast.makeText(this, getString(R.string.game_over_win), Toast.LENGTH_SHORT).show();
                addEventCalendar.insertarVictoria(this);
                //gallery();

                // 🚀 Tomar y guardar la captura de pantalla al ganar
                View rootView = getWindow().getDecorView().getRootView();
                Bitmap screenshot = ScreenshotUtils.takeScreenshot(rootView);
                ScreenshotUtils.saveImageToGallery(this, screenshot);
            }
        } else if (currentBalance <= 0) {
            // Si se ha quedado sin dinero
            Toast.makeText(this, getString(R.string.game_over_no_money), Toast.LENGTH_SHORT).show();
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
