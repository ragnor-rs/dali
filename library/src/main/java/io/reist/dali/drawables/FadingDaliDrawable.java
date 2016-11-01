package io.reist.dali.drawables;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Reist on 01.11.16.
 */

public class FadingDaliDrawable extends DaliDrawable {

    public static final float FADE_DURATION = 5000;

    private float progress = 1;
    private long startTime = -1;
    private int originalAlpha;

    protected boolean fadingIn;

    @Nullable
    private final Drawable placeholder;

    @Nullable
    private final Bitmap.Config placeholderConfig;

    private BitmapShader placeholderShader;
    private int placeholderWidth;
    private int placeholderHeight;

    public FadingDaliDrawable(
            @NonNull Bitmap bitmap,
            @Nullable Drawable placeholder,
            @Nullable Bitmap.Config placeholderConfig
    ) {

        super(bitmap);

        this.placeholder = placeholder;
        this.placeholderConfig = placeholderConfig;

        fadingIn = true;

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
        } else {
            progress = 1;
        }

        if (placeholder != null && progress < 1f) {

            canvas.save();

            transform(canvas, placeholderWidth, placeholderHeight);

            paint.setColorFilter(getColorFilter());
            paint.setAlpha((int) ((1f - progress) * originalAlpha));
            paint.setShader(placeholderShader);

            drawPlaceholder(canvas);

            canvas.restore();

        }

        super.draw(canvas);

        if (fadingIn) {
            invalidateSelf();
        }

    }

    protected void drawPlaceholder(@NonNull Canvas canvas) {
        canvas.drawRect(0, 0, placeholderWidth, placeholderHeight, paint);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {

        super.setBounds(left, top, right, bottom);

        if (placeholder == null) {
            return;
        }

        int intrinsicWidth = placeholder.getIntrinsicWidth();
        int intrinsicHeight = placeholder.getIntrinsicHeight();

        placeholderWidth = intrinsicWidth == -1 ? right - left : intrinsicWidth;
        placeholderHeight = intrinsicHeight == -1 ? bottom - top : intrinsicHeight;
        placeholder.setBounds(0, 0, placeholderWidth, placeholderHeight);

        Bitmap bitmap = Bitmap.createBitmap(placeholderWidth, placeholderHeight, placeholderConfig);
        Canvas canvas = new Canvas(bitmap);
        placeholder.draw(canvas);
        placeholderShader = new BitmapShader(
                bitmap,
                Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP
        );

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

}
