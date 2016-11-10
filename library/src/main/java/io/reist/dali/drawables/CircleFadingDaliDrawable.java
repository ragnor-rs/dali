package io.reist.dali.drawables;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reist.dali.ScaleMode;

public class CircleFadingDaliDrawable extends FadingDaliDrawable {

    public CircleFadingDaliDrawable(
            @NonNull Bitmap bitmap,
            @NonNull ScaleMode scaleMode,
            float dstWidth,
            float dstHeight,
            @Nullable Drawable placeholder,
            @Nullable Bitmap.Config placeholderConfig,
            boolean noFade
    ) {

        super(bitmap, scaleMode, dstWidth, dstHeight, placeholder, placeholderConfig);

        fadingIn = !noFade;

    }

    @Override
    protected void drawBitmap(@NonNull Canvas canvas) {
        canvas.drawCircle(
                srcWidth / 2,
                srcHeight / 2,
                Math.min(srcWidth, srcHeight) / 2,
                paint
        );
    }

}
