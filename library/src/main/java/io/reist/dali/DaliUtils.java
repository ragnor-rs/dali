package io.reist.dali;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Reist on 01.11.16.
 */

public class DaliUtils {

    static void setPlaceholder(
            @NonNull ImageRequestBuilder builder,
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

    static Drawable getPlaceholder(
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

    static void setDrawable(
            @NonNull Drawable drawable,
            @NonNull View view
    ) {
        if (view instanceof ImageView) {
            ((ImageView) view).setImageDrawable(drawable);
        } else {
            throw new UnsupportedOperationException("Cannot set foreground for " + view);
        }
    }

    @SuppressLint("NewApi")
    static void setBackground(
            @NonNull Drawable background,
            @NonNull View view
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(background);
        } else {
            view.setBackground(background);
        }
    }

}
