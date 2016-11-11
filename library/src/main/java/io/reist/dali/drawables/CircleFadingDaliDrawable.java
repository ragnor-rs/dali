package io.reist.dali.drawables;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reist.dali.ScaleMode;

public class CircleFadingDaliDrawable extends FadingDaliDrawable {

    public CircleFadingDaliDrawable(
            @Nullable Bitmap bitmap,
            @NonNull ScaleMode scaleMode,
            float targetWidth,
            float targetHeight,
            @Nullable Drawable placeholder,
            @Nullable Bitmap placeholderBitmap,
            boolean noFade
    ) {
        super(bitmap, scaleMode, targetWidth, targetHeight, placeholder, placeholderBitmap, noFade);
    }

    @Override
    protected void drawBitmap(@NonNull Canvas canvas, RectF dst, Paint paint) {
        float radius = Math.min(targetWidth, targetHeight) / 2;
        canvas.drawCircle(
                dst.centerX(),
                dst.centerY(),
                radius,
                paint
        );
    }

    @Override
    protected void drawPlaceholder(@NonNull Canvas canvas, RectF dst, Paint paint) {
        float radius = Math.min(targetWidth, targetHeight) / 2;
        canvas.drawCircle(
                dst.centerX(),
                dst.centerY(),
                radius,
                paint
        );
    }

}
