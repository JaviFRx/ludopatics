package com.example.ludopatics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CirculosView extends View {

    private Paint paint;
    private int circleColor = Color.RED; // Color predeterminado

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
}
