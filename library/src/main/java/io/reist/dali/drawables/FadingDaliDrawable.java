package io.reist.dali.drawables;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reist.dali.DaliUtils;
import io.reist.dali.ScaleMode;

public class FadingDaliDrawable extends DaliDrawable {

    public static final float FADE_DURATION = 400; // todo move to ImageRequest as a parameter

    private float progress = 1;
    private long startTime = -1;
    private int originalAlpha;

    private boolean fadingIn;

    private final float placeholderWidth;
    private final float placeholderHeight;
    private final RectF placeholderDst = new RectF();

    @Nullable
    private Bitmap placeholderBitmap;

    @Nullable
    private Paint placeholderPaint;

    public FadingDaliDrawable(
            @Nullable Bitmap bitmap,
            @NonNull ScaleMode scaleMode,
            float targetWidth,
            float targetHeight,
            @Nullable Drawable placeholder,
            @Nullable Bitmap placeholderBitmap,
            boolean noFade
    ) {

        super(bitmap, scaleMode, targetWidth, targetHeight);

        this.placeholderBitmap = placeholderBitmap;

        if (noFade) {
            progress = bitmap == null ? 0 : 1;
            originalAlpha = 255;
        } else {
            fadingIn = true;
        }

        if (placeholder == null) {

            placeholderWidth = -1f;
            placeholderHeight = -1f;

        } else {

            placeholderWidth = DaliUtils.getPlaceholderWidth(targetWidth, placeholder);
            placeholderHeight = DaliUtils.getPlaceholderHeight(targetHeight, placeholder);
            if (placeholderWidth <= 0 || placeholderHeight <= 0) {
                return;
            }

            if (placeholderBitmap == null) {
                placeholderBitmap = Bitmap.createBitmap(
                        (int) placeholderWidth,
                        (int) placeholderHeight,
                        DaliUtils.getSafeConfig(bitmap)
                );
            }
            Canvas canvas = new Canvas(placeholderBitmap);
            placeholder.setBounds(0, 0, (int) placeholderWidth, (int) placeholderHeight);
            placeholder.draw(canvas);
            BitmapShader placeholderShader = new BitmapShader(
                    placeholderBitmap,
                    Shader.TileMode.CLAMP,
                    Shader.TileMode.CLAMP
            );
            transform(placeholderWidth, placeholderHeight, placeholderShader, placeholderDst);

            placeholderPaint = new Paint();
            placeholderPaint.setShader(placeholderShader);

        }

    }

    @SuppressLint("NewApi")
    @Override
    public void draw(@NonNull Canvas canvas) {

        if (fadingIn) {
            if (startTime == -1) {
                progress = 0;
                startTime = System.currentTimeMillis();
                originalAlpha = getAlpha();
                setAlpha(0);
            } else {
                progress = (System.currentTimeMillis() - startTime) / FADE_DURATION;
                if (progress >= 1f) {
                    progress = 1f;
                    startTime = -1;
                    setAlpha(originalAlpha);
                    fadingIn = false;
                } else {
                    setAlpha((int) (originalAlpha * progress));
                }
            }
        }

        if (placeholderWidth > 0 && placeholderHeight > 0 && progress < 1f && placeholderPaint != null) {

            placeholderPaint.setColorFilter(getColorFilter());
            placeholderPaint.setAlpha((int) ((1f - progress) * originalAlpha));

            drawPlaceholder(canvas, placeholderDst, placeholderPaint);

        }

        super.draw(canvas);

        if (fadingIn) {
            invalidateSelf();
        }

    }

    @SuppressWarnings("WeakerAccess")
    protected void drawPlaceholder(@NonNull Canvas canvas, RectF dst, Paint paint) {
        canvas.drawRect(dst, paint);
    }

    public boolean isFadingIn() {
        return fadingIn;
    }

    public long getStartTime() {
        return startTime;
    }

    public float getProgress() {
        return progress;
    }

    /*
    @Override
    public void recycle() {

        super.recycle();

        placeholderPaint = null;

        if (placeholderBitmap != null) {
            placeholderBitmap.recycle();
            placeholderBitmap = null;
        }

    }
    */

}
