package com.zvooq.dali.drawables;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;

/**
 * Created by m039 on 12/28/15.
 */
public class FadingBitmapDrawable extends BitmapDrawable {

    private static final float FADE_DURATION = 200f; //ms

    private Drawable mPlaceholder;
    private long mStartTimeMillis;
    private boolean mAnimating;

    public FadingBitmapDrawable(Context context, Bitmap bitmap, Drawable placeholder, boolean noFade) {
        super(context.getResources(), bitmap);
        if (!noFade) {
            mPlaceholder = placeholder;
            mAnimating = true;
            mStartTimeMillis = SystemClock.uptimeMillis();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (!mAnimating) {
            draw(canvas, 1);
        } else {

            float normalized = (SystemClock.uptimeMillis() - mStartTimeMillis) / FADE_DURATION;

            if (normalized >= 1f) {
                normalized = 1;
                mPlaceholder = null;
                mAnimating = false;
            }

            if (mPlaceholder != null) {
                drawPlaceholder(canvas, mPlaceholder, normalized);
            }

            draw(canvas, normalized);

            invalidateSelf();

        }
    }

    @Override
    public void setAlpha(int alpha) {
        if (mPlaceholder != null) {
            mPlaceholder.setAlpha(alpha);
        }
        super.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        if (mPlaceholder != null) {
            mPlaceholder.setColorFilter(cf);
        }
        super.setColorFilter(cf);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        if (mPlaceholder != null) {
            mPlaceholder.setBounds(bounds);
        }
        super.onBoundsChange(bounds);
    }

    /**
     * @param canvas canvas to draw in
     * @param normalized the value from [0 to 1]
     */
    protected void draw(Canvas canvas, float normalized) {
        int partialAlpha = (int) (0xff * normalized);
        Paint paint = getPaint();
        int alpha = paint.getAlpha();
        if (alpha != partialAlpha) {
            paint.setAlpha(partialAlpha);
        }
        super.draw(canvas);
    }

    protected void drawPlaceholder(Canvas canvas, Drawable placeholder, float normalized) {

        int oldAlpha = -1;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            oldAlpha = placeholder.getAlpha();
        }

        placeholder.setAlpha((int) (0xff * (1 - normalized)));

        placeholder.draw(canvas);

        if (oldAlpha != -1) {
            placeholder.setAlpha(oldAlpha);
        }

    }

}
