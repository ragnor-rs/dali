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
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;

/**
 * Renders a vector drawable into a common {@link BitmapDrawable}.
 *
 * Created by Reist on 02.06.16.
 */
public class BitmapCompat {

    private static final BitmapCompatApi IMPL;

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            IMPL = new BitmapCompat21();
        } else {
            IMPL = new BitmapCompatBase();
        }
    }

    /**
     * Converts the given drawable resource to a bitmap representation. Unlike
     * {@link android.graphics.BitmapFactory#decodeResource(Resources, int)}, this method is
     * capable of handling {@link VectorDrawable}s on devices with API level above or equal to
     * {@link android.os.Build.VERSION_CODES#LOLLIPOP}. On older devices, the method may throw
     * {@link IllegalAccessException} if the specified drawable is not a {@link BitmapDrawable}.
     */
    public static Bitmap toBitmap(Context context, int drawableId) {
        Drawable drawable = getDrawable(context, drawableId);
        return IMPL.toBitmap(drawable);
    }

    public static Drawable getDrawable(Context context, int drawableId) {
        Drawable drawable;
        try {
            drawable = ContextCompat.getDrawable(context, drawableId);
        } catch (Resources.NotFoundException exception) {
            drawable = VectorDrawableCompat.create(context.getResources(), drawableId, null);
        }
        return drawable;
    }

    interface BitmapCompatApi {
        Bitmap toBitmap(Drawable drawable);
    }

    private static class BitmapCompat21 implements BitmapCompatApi {

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public Bitmap toBitmap(Drawable drawable) {
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            } else {
                Bitmap bitmap = Bitmap.createBitmap(
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        Bitmap.Config.ARGB_8888
                );
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
                return bitmap;
            }
        }

    }

    private static class BitmapCompatBase implements BitmapCompatApi {

        @Override
        public Bitmap toBitmap(Drawable drawable) {
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            }  else {
                Bitmap bitmap = Bitmap.createBitmap(
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        Bitmap.Config.RGB_565
                );
                Canvas canvas = new Canvas(bitmap);
                drawable.draw(canvas);
                return bitmap;
            }
        }

    }

}
