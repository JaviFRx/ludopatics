package com.example.ludopatics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

public class CirculosView extends View {

    private Paint paint;
    private int circleColor = Color.RED; // Color predeterminado
    private TextPaint textPaint;
    private String text = "";
    public CirculosView(Context context) {
        super(context);
        init();
    }

    public CirculosView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CirculosView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(circleColor);

        // Pintura para el texto
        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE); // Color del texto
        textPaint.setTextSize(40); // Tamaño del texto
        textPaint.setTextAlign(Paint.Align.CENTER); // Alineación centrada

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Centrar el círculo en la vista
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        float radius = Math.min(centerX, centerY) * 0.8f;

        // Dibujar el círculo con el color actual
        canvas.drawCircle(centerX, centerY, radius, paint);
        // Texto en el centro del círculo
        if (!text.isEmpty()) {
            float textY = centerY - ((textPaint.descent() + textPaint.ascent()) / 2); // Centrar verticalmente
            canvas.drawText(text, centerX, textY, textPaint);
        }
    }

    // Método para cambiar el color del círculo
    public void setCircleColor(int color) {
        this.circleColor = color;
        paint.setColor(color);
        invalidate(); // Vuelve a dibujar la vista
    }
    public int getCircleColor() {
        return circleColor;
    }

    public void setText(String text) {
        this.text = text;
        invalidate(); // Vuelve a dibujar la vista
    }

    public String getText() {
        return text;
    }
}
