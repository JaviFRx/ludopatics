package com.example.ludopatics;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.imageview.ShapeableImageView;

public class PerfilActivity extends AppCompatActivity {

    private ShapeableImageView imgPerfil;
    private TextView tvNombrePerfil, tvPrecisionColor, tvPrecisionParImpar, tvPrecisionNumero;

    private DatabaseHelper dbHelper;
    private String nombreUsuario = "";
    private int usuarioId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // Referencias a vistas
        imgPerfil = findViewById(R.id.imgPerfil);
        tvNombrePerfil = findViewById(R.id.tvNombrePerfil);
        tvPrecisionColor = findViewById(R.id.tvPrecisionColor);
        tvPrecisionParImpar = findViewById(R.id.tvPrecisionParImpar);
        tvPrecisionNumero = findViewById(R.id.tvPrecisionNumero);

        // Botón de volver
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Recibir nombre del intent
        nombreUsuario = getIntent().getStringExtra("nombreUsuario");
        if (nombreUsuario == null) {
            nombreUsuario = "Invitado";
        }

        tvNombrePerfil.setText(nombreUsuario);
        imgPerfil.setImageResource(R.drawable.ic_usuario_default); // Imagen circular de perfil

        // Inicializar base de datos y obtener ID
        dbHelper = new DatabaseHelper(this);
        usuarioId = dbHelper.obtenerIdJugador(nombreUsuario);

        // Cargar y mostrar estadísticas
        cargarEstadisticas();
    }


    private void cargarEstadisticas() {
        Cursor cursor = dbHelper.obtenerHistorico(usuarioId);

        int totalColor = 0, aciertosColor = 0;
        int totalParImpar = 0, aciertosParImpar = 0;
        int totalNumero = 0, aciertosNumero = 0;

        if (cursor != null && cursor.moveToFirst()) {
            int colApuesta = cursor.getColumnIndex("apuesta");
            int colGano = cursor.getColumnIndex("gano");

            do {
                if (colApuesta >= 0 && colGano >= 0) {
                    int apuesta = cursor.getInt(colApuesta);
                    int gano = cursor.getInt(colGano);

                    // Clasificar tipo de apuesta según valor numérico
                    if (apuesta <= 36) { // Apuesta a número
                        totalNumero++;
                        if (gano == 1) aciertosNumero++;
                    } else if (apuesta == 100 || apuesta == 101) { // Par o impar
                        totalParImpar++;
                        if (gano == 1) aciertosParImpar++;
                    } else { // Color (rojo = 200, negro = 201, verde = 202)
                        totalColor++;
                        if (gano == 1) aciertosColor++;
                    }
                }
            } while (cursor.moveToNext());

            cursor.close();
        }

        // Calcular porcentajes
        int pctColor = (totalColor > 0) ? aciertosColor * 100 / totalColor : 0;
        int pctParImpar = (totalParImpar > 0) ? aciertosParImpar * 100 / totalParImpar : 0;
        int pctNumero = (totalNumero > 0) ? aciertosNumero * 100 / totalNumero : 0;

        // Mostrar en la interfaz
        tvPrecisionColor.setText("Color: " + pctColor + "%");
        tvPrecisionParImpar.setText("Par/Impar: " + pctParImpar + "%");
        tvPrecisionNumero.setText("Número: " + pctNumero + "%");
    }


}