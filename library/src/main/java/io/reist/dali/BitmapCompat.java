package io.reist.dali;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;

/**
 * Renders a vector drawable into a common {@link BitmapDrawable}.
 *
 * Created by Reist on 02.06.16.
 */
public class BitmapCompat {

    public static final DrawableToBitmap IMPL;

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            IMPL = new DrawableToBitmap21();
        } else {
            IMPL = new DrawableToBitmapBase();
        }
    }

    /**
     * Converts the given drawable resource to a bitmap representation. Unlike
     * {@link android.graphics.BitmapFactory#decodeResource(Resources, int)}, this method is
     * capable of rasterizing {@link VectorDrawable}s on devices with API level above or equal to
     * {@link android.os.Build.VERSION_CODES#LOLLIPOP}. On older devices, the method may throw
     * {@link IllegalAccessException} if the specified drawable is not a {@link BitmapDrawable}.
     */
    public static Bitmap toBitmap(Context context, int drawableId) {
        return IMPL.toBitmap(ContextCompat.getDrawable(context, drawableId));
    }

    interface DrawableToBitmap {
        Bitmap toBitmap(Drawable drawable);
    }

    private static class DrawableToBitmap21 implements DrawableToBitmap {

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public Bitmap toBitmap(Drawable drawable) {
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            } else if (drawable instanceof VectorDrawable) {
                VectorDrawable vectorDrawable = (VectorDrawable) drawable;
                Bitmap bitmap = Bitmap.createBitmap(
                        vectorDrawable.getIntrinsicWidth(),
                        vectorDrawable.getIntrinsicHeight(),
                        Bitmap.Config.ARGB_8888
                );
                Canvas canvas = new Canvas(bitmap);
                vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                vectorDrawable.draw(canvas);
                return bitmap;
            }  else {
                throw new IllegalArgumentException("Unsupported drawable: " + drawable);
            }
        }

    }

    private static class DrawableToBitmapBase implements DrawableToBitmap {

        @Override
        public Bitmap toBitmap(Drawable drawable) {
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            }  else {
                throw new IllegalArgumentException("Unsupported drawable: " + drawable);
            }
        }

    }

}
