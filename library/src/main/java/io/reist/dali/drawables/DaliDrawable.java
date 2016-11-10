package io.reist.dali.drawables;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import io.reist.dali.ScaleMode;

public class DaliDrawable extends Drawable {

    @NonNull
    private final BitmapShader bitmapShader;

    private final ScaleMode scaleMode;

    private final float dstWidth;
    private final float dstHeight;

    private int alpha = 255;
    private ColorFilter colorFilter = null;

    final Paint paint = new Paint();

    final float srcWidth;
    final float srcHeight;

    public DaliDrawable(
            @NonNull Bitmap bitmap,
            @NonNull ScaleMode scaleMode,
            float dstWidth,
            float dstHeight
    ) {

        bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        srcWidth = bitmap.getWidth();
        srcHeight = bitmap.getHeight();

        this.scaleMode = scaleMode;
        this.dstWidth = dstWidth;
        this.dstHeight = dstHeight;

    }

    @Override
    public void draw(@NonNull Canvas canvas) {

        if (alpha == 0 || srcWidth <= 0 || srcHeight <= 0 || dstWidth <= 0 || dstHeight <= 0) {
            return;
        }

        canvas.save();
        drawImage(canvas);
        canvas.restore();

    }

    @CallSuper
    protected void drawImage(@NonNull Canvas canvas) {

        transform(canvas, srcWidth, srcHeight);

        paint.setColorFilter(colorFilter);
        paint.setAlpha(alpha);
        paint.setShader(bitmapShader);

        drawBitmap(canvas);

    }

    @SuppressWarnings("SuspiciousNameCombination")
    void transform(@NonNull Canvas canvas, float srcWidth, float srcHeight) {

        float scaleX, scaleY;

        Rect bounds = getBounds();

        switch (scaleMode) {

            case CENTER_CROP:
                if (srcWidth * dstHeight > dstWidth * srcHeight) {
                    scaleX = dstHeight / srcHeight;
                } else {
                    scaleX = dstWidth / srcWidth;
                }
                scaleY = scaleX;
                break;

            case CENTER_INSIDE:
                if (srcWidth * dstHeight > dstWidth * srcHeight) {
                    scaleX = dstWidth / srcWidth;
                } else {
                    scaleX = dstHeight / srcHeight;
                }
                scaleY = scaleX;
                break;

            case FIT_XY:
                scaleX = dstWidth / srcWidth;
                scaleY = dstHeight / srcHeight;
                break;

            default:
                throw new IllegalArgumentException("scaleMode = " + scaleMode);

        }

        float finalWidth = srcWidth * scaleX;
        float finalHeight = srcHeight * scaleY;

        float finalLeft = bounds.exactCenterX() - finalWidth / 2;
        float finalTop = bounds.exactCenterY() - finalHeight / 2;

        canvas.translate(finalLeft, finalTop);
        canvas.scale(scaleX, scaleY);

    }

    protected void drawBitmap(@NonNull Canvas canvas) {
        canvas.drawRect(0, 0, srcWidth, srcHeight, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    @Override
    public int getAlpha() {
        return alpha;
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        this.colorFilter = colorFilter;
    }

    @Override
    public int getOpacity() {
        switch (alpha) {

            case 0:
                return PixelFormat.TRANSPARENT;

            case 255:
                return PixelFormat.OPAQUE;

            default:
                return PixelFormat.TRANSLUCENT;

        }
    }

    @Override
    public ColorFilter getColorFilter() {
        return colorFilter;
    }

}
