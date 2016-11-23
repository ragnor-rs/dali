package io.reist.dali;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import io.reist.dali.drawables.CircleFadingDaliDrawable;
import io.reist.dali.drawables.FadingDaliDrawable;

public class DaliUtils {

    public static void setPlaceholder(
            @NonNull ImageRequest request,
            @NonNull View view,
            boolean background,
            @Nullable Bitmap placeholderBitmap
    ) {

        int placeholderRes = request.placeholderRes;

        if (placeholderRes == 0) {
            return;
        }

        Drawable drawable = BitmapCompat.getDrawable(
                view.getContext(),
                placeholderRes
        );

        FadingDaliDrawable placeholderDrawable;

        if (request.inCircle) {
            placeholderDrawable = new CircleFadingDaliDrawable(
                    null,
                    request.scaleMode,
                    view.getWidth() - view.getPaddingLeft() - view.getPaddingRight(),
                    view.getHeight() - view.getPaddingTop() - view.getPaddingBottom(),
                    drawable,
                    placeholderBitmap,
                    true
            );
        } else {
            placeholderDrawable = new FadingDaliDrawable(
                    null,
                    request.scaleMode,
                    view.getWidth() - view.getPaddingLeft() - view.getPaddingRight(),
                    view.getHeight() - view.getPaddingTop() - view.getPaddingBottom(),
                    drawable,
                    placeholderBitmap,
                    true
            );
        }

        if (background) {
            setBackground(placeholderDrawable, view);
        } else {
            setDrawable(placeholderDrawable, view);
        }

    }

    public static Drawable getPlaceholder(
            @NonNull View view,
            boolean background
    ) {
        if (background) {
            return view.getBackground();
        } else {
            if (view instanceof ImageView) {
                return ((ImageView) view).getDrawable();
            } else {
                return null;
            }
        }
    }

    public static void setDrawable(
            @NonNull Drawable drawable,
            @NonNull View view
    ) {

        if (view instanceof ImageView) {

            ImageView imageView = (ImageView) view;

//            Drawable oldDrawable = imageView.getDrawable();
//            if (oldDrawable instanceof DaliDrawable) {
//                ((DaliDrawable) oldDrawable).recycle();
//            }

            imageView.setImageDrawable(drawable);

        }

    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static void setBackground(
            @NonNull Drawable background,
            @NonNull View view
    ) {

//        Drawable oldBackground = view.getBackground();
//        if (oldBackground instanceof DaliDrawable) {
//            ((DaliDrawable) oldBackground).recycle();
//        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(background);
        } else {
            view.setBackground(background);
        }

    }

    public static Context getApplicationContext(@NonNull Object attachTarget) {
        if (attachTarget instanceof Activity) {
            return ((Activity) attachTarget).getApplicationContext();
        } else if (attachTarget instanceof Fragment) {
            Fragment fragment = (Fragment) attachTarget;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return fragment.getContext();
            } else {
                Activity activity = fragment.getActivity();
                return activity == null ? null : activity.getApplicationContext();
            }
        } else if (attachTarget instanceof android.support.v4.app.Fragment) {
            return ((android.support.v4.app.Fragment) attachTarget).getContext();
        } else if (attachTarget instanceof Context) {
            return ((Context) attachTarget).getApplicationContext();
        } else {
            return null;
        }
    }

    public static float getPlaceholderHeight(float targetHeight, @Nullable Drawable placeholder) {
        int intrinsicHeight = -1;
        if (placeholder != null) {
            intrinsicHeight = placeholder.getIntrinsicHeight();
        }
        return intrinsicHeight == -1 ? targetHeight : intrinsicHeight;
    }

    public static float getPlaceholderWidth(float targetWidth, @Nullable Drawable placeholder) {
        int intrinsicWidth = -1;
        if (placeholder != null) {
            intrinsicWidth = placeholder.getIntrinsicWidth();
        }
        return intrinsicWidth == -1 ? targetWidth : intrinsicWidth;
    }

    public static Bitmap.Config getSafeConfig(@Nullable Bitmap bitmap) {
        return bitmap != null && bitmap.getConfig() != null ?
                bitmap.getConfig() :
                Bitmap.Config.ARGB_8888;
    }

    @Nullable
    public static Context getApplicationContext(@NonNull ImageRequest request) {
        Context appContext = null;
        if (request.attachTarget != null) {
            appContext = getApplicationContext(request.attachTarget);
        }
        return appContext;
    }

}
