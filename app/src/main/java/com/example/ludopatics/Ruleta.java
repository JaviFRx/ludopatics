package com.example.ludopatics;

import android.os.Bundle;
import android.net.Uri;
import android.widget.MediaController;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class Ruleta extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ruleta); // Cargar el layout ruleta.xml

        // Encuentra el VideoView en el layout
        VideoView videoView = findViewById(R.id.videoView);

        // Configura la ubicación del archivo de video (ubicación en res/raw)
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/raw/ruleta");

        // Configura el VideoView con el archivo de video
        videoView.setVideoURI(videoUri);

        // Agregar controles de medios (play, pause, etc.)
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        // Hacer que el video se repita en bucle
        videoView.setOnCompletionListener(mp -> videoView.start());

        // Iniciar la reproducción del video
        videoView.start();
    }
}
