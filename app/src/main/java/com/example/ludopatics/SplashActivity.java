package com.example.ludopatics;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Encuentra el ImageView en la pantalla de inicio y establece el GIF usando Glide
        ImageView splashImage = findViewById(R.id.splashImage);

        // Cargar el GIF con Glide
        Glide.with(this)
                .load(R.drawable.presents)
                .into(splashImage);

        // Establecer un retraso para la transición al MainActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Intent para iniciar la MainActivity después de la pantalla de inicio
                Intent intent = new Intent(SplashActivity.this, Ruleta.class);
                startActivity(intent);
                finish(); // Cierra SplashActivity para que no pueda volver a ella
            }
        }, 10000); // Tiempo en milisegundos que el GIF permanecerá (3 segundos en este caso)
    }
}
