package io.reist.dali;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

public class DaliUtils {

    static void setPlaceholder(
            @NonNull ImageRequest builder,
            @NonNull View view,
            boolean background
    ) {

        int placeholderRes = builder.placeholderRes;

        if (placeholderRes == 0) {
            return;
        }

        Drawable drawable = BitmapCompat.getDrawable(
                view.getContext(),
                placeholderRes
        );

        if (background) {
            setBackground(drawable, view);
        } else {
            setDrawable(drawable, view);
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
                throw new UnsupportedOperationException("Cannot get placeholder for " + view);
            }
        }
    }

    public static void setDrawable(
            @NonNull Drawable drawable,
            @NonNull View view
    ) {
        if (view instanceof ImageView) {
            ((ImageView) view).setImageDrawable(drawable);
        } else {
            throw new UnsupportedOperationException("Cannot set foreground for " + view);
        }
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static void setBackground(
            @NonNull Drawable background,
            @NonNull View view
    ) {
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

}
