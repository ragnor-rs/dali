package io.reist.dali;

import android.support.annotation.NonNull;
import android.view.View;

import io.reist.dali.glide.GlideImageLoader;

import static io.reist.dali.DaliUtils.getApplicationContext;
import static io.reist.dali.DaliUtils.setPlaceholder;

public class DaliLoader implements ImageLoader {

    private ImageLoader mMainImageLoader;
    private DeferredImageLoader mDeferredImageLoader;

    private DaliLoader() {
        initMainImageLoader(GlideImageLoader.class);
        initDeferredImageLoader(DeferredImageLoader.class);
    }

    @SuppressWarnings("TryWithIdenticalCatches")
    void initDeferredImageLoader(@NonNull Class<? extends DeferredImageLoader> deferredImageLoaderClass) {

        if (mDeferredImageLoader != null) {
            mDeferredImageLoader.cancelAll();
        }

        try {
            mDeferredImageLoader = deferredImageLoaderClass.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    @SuppressWarnings("TryWithIdenticalCatches")
    void initMainImageLoader(@NonNull Class<? extends ImageLoader> mainImageLoaderClass) {

        if (mMainImageLoader != null) {
            mMainImageLoader.cancelAll();
        }

        try {
            mMainImageLoader = mainImageLoaderClass.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void load(@NonNull ImageRequestBuilder builder, @NonNull View view, boolean background) {

        cancel(view);

        if (builder.transformer != null) {
            builder = builder.transformer.transform(builder);
        }

        if (builder.defer && (builder.targetWidth <= 0 || builder.targetHeight <= 0)) {
            mDeferredImageLoader.load(builder, view, background);
        } else {
            if (builder.url == null) {
                setPlaceholder(builder, view, background);
            } else {
                mMainImageLoader.load(builder, view, background);
            }
        }

    }

    @Override
    public void load(@NonNull ImageRequestBuilder builder, @NonNull DaliCallback callback) {

        cancel(callback);

        if (builder.transformer != null) {
            builder = builder.transformer.transform(builder);
        }

        if (builder.url == null) {
            callback.onImageLoaded(
                    BitmapCompat.toBitmap(getApplicationContext(builder.attachTarget), builder.placeholderRes)
            );
        } else {
            mMainImageLoader.load(builder, callback);
        }

    }

    @Override
    public void cancel(@NonNull Object o) {
        mDeferredImageLoader.cancel(o);
        mMainImageLoader.cancel(o);
    }

    @Override
    public void cancelAll() {
        mDeferredImageLoader.cancelAll();
        mMainImageLoader.cancelAll();
    }

    public static DaliLoader getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public DeferredImageLoader getDeferredImageLoader() {
        return mDeferredImageLoader;
    }

    public ImageLoader getMainImageLoader() {
        return mMainImageLoader;
    }

    /**
     * Used to lazily instantiate Dali in {@link #getInstance()}
     */
    private static class SingletonHolder {
        static final DaliLoader INSTANCE = new DaliLoader();
    }

}
