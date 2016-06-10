package io.reist.dali;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.widget.ImageView;

import java.lang.reflect.InvocationTargetException;

/**
 * Dali is an abstraction above asynchronous image loading libraries. The default implementation
 * uses {@link GlideImageLoader} which fetches images from the network via Glide library
 * (https://github.com/bumptech/glide). The underlying implementation can be changed by calling
 * {@link #setMainImageLoaderClass(Class)} before {@link ImageRequestBuilder#into(View)},
 * {@link ImageRequestBuilder#into(View, boolean)},
 * {@link ImageRequestBuilder#into(DaliCallback, Context)} or {@link #cancelRequest(Object)} are
 * called.
 *
 * Created by m039 on 12/30/15.
 */
public class Dali implements ImageLoader {

    private final ImageLoader mMainImageLoader;
    private final DeferredImageLoader mDeferredImageLoader;

    @SuppressWarnings("TryWithIdenticalCatches")
    private Dali() {
        try {

            mMainImageLoader = sMainImageLoaderClass.newInstance();

            mDeferredImageLoader = sDeferredImageLoaderClass
                    .getConstructor(ImageLoader.class)
                    .newInstance(mMainImageLoader);

        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static Class<? extends ImageLoader> sMainImageLoaderClass = GlideImageLoader.class;
    private static Class<? extends DeferredImageLoader> sDeferredImageLoaderClass = DeferredImageLoader.class;

    static Dali getInstance() {
        return SingletonHolder.INSTANCE;
    }

    DeferredImageLoader getDeferredImageLoader() {
        return mDeferredImageLoader;
    }

    ImageLoader getMainImageLoader() {
        return mMainImageLoader;
    }

    /**
     * Used to lazily instantiate Dali in {@link #getInstance()}
     */
    public static class SingletonHolder {
        public static final Dali INSTANCE = new Dali();
    }

    public static ImageRequestBuilder load(String url) {
        return new ImageRequestBuilder().url(url);
    }

    /**
     * @see ImageLoader#cancel(Object)
     */
    @SuppressWarnings("unused")
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
                mMainImageLoader.load(builder, view, false);
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
            mMainImageLoader.load(builder, callback, context);
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
        mMainImageLoader.cancel(o);
    }


    /**
     * Modifies image fetching mechanism
     */
    @SuppressWarnings("unused")
    public static void setImageFactory(DaliImageFactory daliImageFactory) {
        throw new UnsupportedOperationException();
    }

    /**
     * Changes Dali main loader implementation. This loader will be used for {@link View}s of known
     * dimensions and {@link DaliCallback}.
     *
     * @see GlideImageLoader     default main image loader
     *
     */
    @SuppressWarnings("unused")
    public static void setMainImageLoaderClass(Class<? extends ImageLoader> imageLoaderClass) {
        sMainImageLoaderClass = imageLoaderClass;
    }

    /**
     * Changes Dali deferred image loader implementation. This loader will be used for {@link View}s
     * which have not been measured yet
     *
     * @see DeferredImageLoader     default deferred image loader
     */
    @SuppressWarnings("unused")
    public static void setDeferredImageLoaderClass(Class<? extends DeferredImageLoader> imageLoaderClass) {
        sDeferredImageLoaderClass = imageLoaderClass;
    }

}
