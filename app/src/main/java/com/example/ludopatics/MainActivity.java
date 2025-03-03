package com.example.ludopatics;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

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
        Button btnJugar = findViewById(R.id.btnJugar);

        // Configura el OnClickListener para abrir la actividad "Ruleta"
        btnJugar.setOnClickListener(v -> {
            // Crea un Intent para abrir la actividad Ruleta
            Intent intent = new Intent(MainActivity.this, Ruleta.class);
            startActivity(intent);  // Lanza la actividad Ruleta
        });
    }

}