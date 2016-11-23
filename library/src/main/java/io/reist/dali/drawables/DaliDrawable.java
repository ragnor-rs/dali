package io.reist.dali.drawables;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reist.dali.ScaleMode;

public class DaliDrawable extends Drawable {

    private final ScaleMode scaleMode;

    protected final float targetWidth;
    protected final float targetHeight;

    private int alpha = 255;
    private ColorFilter colorFilter = null;

    private final float bitmapWidth;
    private final float bitmapHeight;
    private final RectF bitmapDst = new RectF();

    @Nullable
    private Paint bitmapPaint;

    public DaliDrawable(
            @Nullable Bitmap bitmap,
            @NonNull ScaleMode scaleMode,
            float targetWidth,
            float targetHeight
    ) {

        this.scaleMode = scaleMode;
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;

        if (bitmap == null) {
            bitmapWidth = -1;
            bitmapHeight = -1;
        } else {

            bitmapWidth = bitmap.getWidth();
            bitmapHeight = bitmap.getHeight();

            BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            transform(bitmapWidth, bitmapHeight, bitmapShader, bitmapDst);

            bitmapPaint = new Paint();
            bitmapPaint.setShader(bitmapShader);

        }

    }

    @Override
    public void draw(@NonNull Canvas canvas) {

        if (alpha == 0 || bitmapWidth <= 0 || !hasBitmap() || targetHeight <= 0 || bitmapPaint == null) {
            return;
        }

        bitmapPaint.setColorFilter(colorFilter);
        bitmapPaint.setAlpha(alpha);

        drawBitmap(canvas, bitmapDst, bitmapPaint);

    }

    @SuppressWarnings("SuspiciousNameCombination")
    void transform(
            float bitmapWidth,
            float bitmapHeight,
            BitmapShader bitmapShader,
            RectF dst
    ) {

        float scaleX, scaleY;

        switch (scaleMode) {

            case CENTER_CROP:
                if (bitmapWidth * targetHeight > targetWidth * bitmapHeight) {
                    scaleX = targetHeight / bitmapHeight;
                } else {
                    scaleX = targetWidth / bitmapWidth;
                }
                scaleY = scaleX;
                break;

            case CENTER_INSIDE:
                if (bitmapWidth * targetHeight > targetWidth * bitmapHeight) {
                    scaleX = targetWidth / bitmapWidth;
                } else {
                    scaleX = targetHeight / bitmapHeight;
                }
                scaleY = scaleX;
                break;

            case FIT_XY:
                scaleX = targetWidth / bitmapWidth;
                scaleY = targetHeight / bitmapHeight;
                break;

            default:
                throw new IllegalArgumentException("scaleMode = " + scaleMode);

        }

        float viewWidthInImage = targetWidth / scaleX;
        float viewHeightInImage = targetHeight / scaleY;

        Matrix bitmapMatrix = new Matrix();
        bitmapMatrix.setTranslate(
                viewWidthInImage / 2f - bitmapWidth / 2f,
                viewHeightInImage / 2f - bitmapHeight / 2f
        );
        bitmapMatrix.postScale(scaleX, scaleY);
        bitmapShader.setLocalMatrix(bitmapMatrix);

        float imageWidthInView = bitmapWidth * scaleX;
        float imageHeightInView = bitmapHeight * scaleY;

        dst.left = targetWidth / 2f - imageWidthInView / 2f;
        dst.top = targetHeight / 2f - imageHeightInView / 2f;
        dst.right = dst.left + imageWidthInView;
        dst.bottom = dst.top + imageHeightInView;

        if (dst.left < 0) dst.left = 0;
        if (dst.top < 0) dst.top = 0;
        if (dst.right > targetWidth) dst.right = targetWidth;
        if (dst.bottom > targetHeight) dst.bottom = targetHeight;

    }

    protected void drawBitmap(@NonNull Canvas canvas, RectF dst, Paint bitmapPaint) {
        canvas.drawRect(dst, bitmapPaint);
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

    public boolean hasBitmap() {
        return bitmapWidth > 0 && bitmapHeight > 0;
    }

    /*
    public void recycle() {

        bitmapPaint = null;

        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }

    }
    */

}
