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
import android.support.annotation.NonNull;

public class DaliDrawable extends Drawable {

    @NonNull
    private final BitmapShader bitmapShader;

    private int alpha = 255;
    private ColorFilter colorFilter = null;

    final Paint paint = new Paint();

    final float srcWidth;
    final float srcHeight;

    public DaliDrawable(@NonNull Bitmap bitmap) {
        bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        srcWidth = bitmap.getWidth();
        srcHeight = bitmap.getHeight();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {

        if (alpha == 0 || srcWidth <= 0 || srcHeight <= 0) {
            return;
        }

        canvas.save();

        transform(canvas, srcWidth, srcHeight);

        paint.setColorFilter(colorFilter);
        paint.setAlpha(alpha);
        paint.setShader(bitmapShader);

        drawBitmap(canvas);

        canvas.restore();

    }

    void transform(@NonNull Canvas canvas, float srcWidth, float srcHeight) {

        float scale;

        Rect bounds = getBounds();

        float dstWidth = bounds.width();
        float dstHeight = bounds.height();

        if (srcWidth * dstHeight > dstWidth * srcHeight) {
            scale = dstWidth / srcWidth;
        } else {
            scale = dstHeight / srcHeight;
        }

        float finalWidth = srcWidth * scale;
        float finalHeight = srcHeight * scale;

        float finalLeft = bounds.exactCenterX() - finalWidth / 2;
        float finalTop = bounds.exactCenterY() - finalHeight / 2;

        canvas.translate(finalLeft, finalTop);
        canvas.scale(scale, scale);

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
