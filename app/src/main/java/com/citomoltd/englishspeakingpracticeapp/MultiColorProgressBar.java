package com.citomoltd.englishspeakingpracticeapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class MultiColorProgressBar extends View {

    private Paint paint;
    private int progress = 0;  // Progress value
    private int maxProgress = 100;  // Maximum progress
    private float radius;
    private int strokeWidth = 30;  // Width of the progress circle

    public MultiColorProgressBar(Context context) {
        super(context);
        init();
    }

    public MultiColorProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MultiColorProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(Color.GRAY);  // Default background color
    }

    public void setProgress(int progress) {
        this.progress = Math.max(0, Math.min(progress, maxProgress));
        invalidate();  // Trigger redraw
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        radius = Math.min(w, h) / 2 - strokeWidth;  // Set the radius for the circle
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Calculate the center of the circle
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        // Draw the background circle (gray)
        paint.setColor(0xFFE0E0E0); // Light Gray
        canvas.drawCircle(centerX, centerY, radius, paint);

        // Draw the progress circle (multi-color)
        paint.setStrokeCap(Paint.Cap.ROUND);
        float angle = 360 * progress / (float) maxProgress;

        // First segment (Red)
        paint.setColor(0xFFFF0000);  // Red
        canvas.drawArc(centerX - radius, centerY - radius, centerX + radius, centerY + radius,
                -90, angle / 3, false, paint);

        // Second segment (Yellow)
        paint.setColor(0xFFFFFF00);  // Yellow
        canvas.drawArc(centerX - radius, centerY - radius, centerX + radius, centerY + radius,
                -90 + angle / 3, angle / 3, false, paint);

        // Third segment (Green)
        paint.setColor(0xFF00FF00);  // Green
        canvas.drawArc(centerX - radius, centerY - radius, centerX + radius, centerY + radius,
                -90 + 2 * angle / 3, angle / 3, false, paint);
    }
}
