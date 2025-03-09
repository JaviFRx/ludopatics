package com.example.ludopatics;

import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;  // Importa TextView
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;
import android.util.Log;

public class Gira extends AppCompatActivity {

    private ImageView ruletaImage;
    private Button btnGirar;
    private TextView textViewNumero;  // Declaramos el TextView

    // Definimos los números de la ruleta en orden
    private final String[] casillasRuleta = {
            "10", "5", "24", "16", "33", "1", "20", "14", "31", "9", "22", "18",
            "29", "7", "28", "12", "35", "3", "26", "0", "32", "15", "19", "4",
            "21", "2", "25", "17", "34", "6", "27", "13", "36", "11", "30", "8", "23"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ruleta2);

        // Inicializar las vistas
        ruletaImage = findViewById(R.id.ruletaImage);
        btnGirar = findViewById(R.id.btnGirar);
        textViewNumero = findViewById(R.id.textViewNumero);  // Referencia al TextView

        btnGirar.setOnClickListener(view -> girarRuleta());
    }

    private void girarRuleta() {
        Random random = new Random();
        int vueltas = 5; // Cantidad de vueltas completas
        int angle = random.nextInt(360) + (360 * vueltas); // Ángulo final

        // Crear la animación con inicio rápido y desaceleración progresiva
        RotateAnimation rotateAnimation = new RotateAnimation(0, angle,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        rotateAnimation.setDuration(4000); // Duración más larga (4 segundos)
        rotateAnimation.setFillAfter(true); // Mantener la posición final
        rotateAnimation.setInterpolator(new AccelerateDecelerateInterpolator()); // Aceleración al inicio y desaceleración progresiva

        // Establecer un listener para cuando la animación termine
        rotateAnimation.setAnimationListener(new android.view.animation.Animation.AnimationListener() {
            @Override
            public void onAnimationStart(android.view.animation.Animation animation) {
                // No necesitamos hacer nada cuando empieza la animación
            }

            @Override
            public void onAnimationEnd(android.view.animation.Animation animation) {
                // Calcular la casilla final después de que termine la animación
                calcularCasilla(angle);
            }

            @Override
            public void onAnimationRepeat(android.view.animation.Animation animation) {
                // No necesitamos hacer nada cuando la animación se repite
            }
        });

        // Iniciar la animación de la ruleta
        ruletaImage.startAnimation(rotateAnimation);
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
        int indice = numCasillas - 1 - (int)(((normalizedAngle + offsetAngulo) % 360) / anguloPorCasilla);

        // Ajustar por el desplazamiento de una posición
        indice = (indice + 1) % numCasillas;

        // Asegurar que el índice esté dentro del rango
        indice = (indice + numCasillas) % numCasillas;

        // Obtener el número de la casilla
        String casillaFinal = casillasRuleta[indice];

        // Actualizar el TextView con el número final
        textViewNumero.setText("Número: " + casillaFinal);

        // Para depuración
        Log.d("Ruleta", "Ángulo: " + normalizedAngle + ", Índice: " + indice + ", Número: " + casillaFinal);
    }
}
