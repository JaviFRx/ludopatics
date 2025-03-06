package com.example.ludopatics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CirculosView extends View {

    private Paint redPaint;
    private Paint blackPaint;

    public CirculosView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        invalidate();
    }


    private void init() {
        redPaint = new Paint();
        redPaint.setColor(Color.RED);
        redPaint.setAntiAlias(true);

        blackPaint = new Paint();
        blackPaint.setColor(Color.BLACK);
        blackPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        float radius = Math.min(centerX, centerY) * 0.4f;

        // Dibujar el círculo rojo (izquierda)
        canvas.drawCircle(centerX - radius * 1.2f, centerY, radius, redPaint);

        // Dibujar el círculo negro (derecha)
        canvas.drawCircle(centerX + radius * 1.2f, centerY, radius, blackPaint);
    }
}
