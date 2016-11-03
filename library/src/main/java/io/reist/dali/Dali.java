package io.reist.dali;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import io.reist.dali.glide.GlideImageLoader;

/**
 * Dali is an abstraction above asynchronous image loading libraries. The default implementation
 * uses {@link GlideImageLoader} which fetches images from the network via Glide library
 * (https://github.com/bumptech/glide). The underlying implementation can be changed by calling
 * {@link #setMainImageLoaderClass(Class)} before {@link ImageRequest#into(View)},
 * {@link ImageRequest#into(View, boolean)},
 * {@link ImageRequest#into(DaliCallback)} or {@link #cancel(Object)} are
 * called.
 *
 * Created by m039 on 12/30/15.
 */
public class Dali {

    private final Object attachTarget;

    private Dali(Object attachTarget) {
        this.attachTarget = attachTarget;
    }

    public ImageRequest load(String url) {
        return new ImageRequest(attachTarget).imageLoader(DaliLoader.getInstance()).url(url);
    }

    public static Dali with(@NonNull Context context) {
        return new Dali(context);
    }

    public static Dali with(@NonNull android.app.Fragment fragment) {
        return new Dali(fragment);
    }

    public static Dali with(@NonNull android.support.v4.app.Fragment fragment) {
        return new Dali(fragment);
    }

    /**
     * @see ImageLoader#cancel(Object)
     */
    @SuppressWarnings("unused")
    public static void cancel(@NonNull Object target) {
        DaliLoader.getInstance().cancel(target);
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
        DaliLoader.getInstance().initMainImageLoader(imageLoaderClass);
    }

    /**
     * Changes Dali deferred image loader implementation. This loader will be used for {@link View}s
     * which have not been measured yet
     *
     * @see DeferredImageLoader     default deferred image loader
     */
    @SuppressWarnings("unused")
    public static void setDeferredImageLoaderClass(Class<? extends DeferredImageLoader> imageLoaderClass) {
        DaliLoader.getInstance().initDeferredImageLoader(imageLoaderClass);
    }

}
