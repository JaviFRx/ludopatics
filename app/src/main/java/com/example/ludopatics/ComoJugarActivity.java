package com.example.ludopatics;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class ComoJugarActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_como_jugar);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        WebView webView = findViewById(R.id.webView);

        String htmlContent = "<html>" +
                "<head>" +
                "<style>" +
                "body { background-color: transparent; color: #FFFFFF; font-family: sans-serif; padding: 16px; }" +
                "h1 { text-align: center; font-size: 34px; margin-bottom: 20px; }" +
                "p { font-size: 20px; margin-bottom: 12px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<h1>" + getString(R.string.title_como_jugar) + "</h1>" +
                "<p>1. " + getString(R.string.primer_paso) + "</p>" +
                "<p>2. " + getString(R.string.segundo_paso) + "</p>" +
                "<p>" + getString(R.string.instruccion_final) + "</p>" +
                "</body>" +
                "</html>";

        webView.setBackgroundColor(0x00000000); // fondo transparente
        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);


    }
}