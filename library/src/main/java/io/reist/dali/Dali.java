package io.reist.dali;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by m039 on 12/30/15.
 */
public class Dali implements ImageLoader {

    private final ImageLoader mDeferredImageLoader = new DeferredImageLoader();
    private final ImageLoader mImageLoader = new GlideImageLoader();

    private Dali() {}

    private static final Dali sDali = new Dali();

    static Dali getInstance() {
        return sDali;
    }

    public static ImageRequestBuilder load(String url) {
        return new ImageRequestBuilder().url(url);
    }

    /**
     * @see ImageLoader#cancel(Object)
     */
    public static void cancelRequest(Object o) {
        getInstance().cancel(o);
    }

    @Override
    public void load(ImageRequestBuilder builder, View view, boolean background) {

        if (view == null) {
            return;
        }

        cancel(view);

        if (builder.defer && (builder.targetWidth <= 0 || builder.targetHeight <= 0)) {
            mDeferredImageLoader.load(builder, view, false);
        } else {

            if (builder.transformer != null) {
                builder = builder.transformer.transform(builder);
            }

            if (builder.url == null) {
                setPlaceholder(builder.placeholderRes, view, background);
            } else {
                mImageLoader.load(builder, view, false);
            }

        }

    }

    @Override
    public void load(ImageRequestBuilder builder, DaliCallback callback, Context context) {

        if (callback == null) {
            return;
        }

        cancel(callback);

        if (builder.transformer != null) {
            builder = builder.transformer.transform(builder);
        }

        if (builder.url == null) {
            setPlaceholder(builder.placeholderRes, callback, context);
        } else {
            mImageLoader.load(builder, callback, context);
        }

    }

    static void setPlaceholder(@DrawableRes int placeholderRes, DaliCallback callback, Context context) {
        callback.onImageLoaded(BitmapCompat.toBitmap(context, placeholderRes));
    }

    static void setPlaceholder(@DrawableRes int placeholderRes, View view, boolean background) {
        if (background) {

            if (placeholderRes == 0) {
                setBackground(view, null);
            } else {
                view.setBackgroundResource(placeholderRes);
            }

        } else {

            if (view instanceof ImageView) {
                ImageView imageView = (ImageView) view;
                if (placeholderRes == 0) {
                    imageView.setImageDrawable(null);
                } else {
                    imageView.setImageResource(placeholderRes);
                }
            } else {
                throw new UnsupportedOperationException("Cannot set foreground for " + view);
            }

        }
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    static void setBackground(View view, Drawable background) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(background);
        } else {
            view.setBackground(background);
        }
    }

    @Override
    public void cancel(Object o) {
        mDeferredImageLoader.cancel(o);
        mImageLoader.cancel(o);
    }

}
