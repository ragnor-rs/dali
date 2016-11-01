package io.reist.dali.drawables;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Reist on 01.11.16.
 */

public class CircleFadingDaliDrawable extends FadingDaliDrawable {

    public CircleFadingDaliDrawable(
            @NonNull Bitmap bitmap,
            @Nullable Drawable placeholder,
            @Nullable Bitmap.Config placeholderConfig,
            boolean noFade
    ) {
        super(bitmap, placeholder, placeholderConfig);
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
