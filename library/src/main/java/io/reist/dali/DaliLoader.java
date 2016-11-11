package io.reist.dali;

import android.support.annotation.NonNull;
import android.view.View;

import io.reist.dali.glide.GlideImageLoader;

import static io.reist.dali.DaliUtils.getApplicationContext;
import static io.reist.dali.DaliUtils.setPlaceholder;

public class DaliLoader implements ImageLoader {

    private ImageLoader mMainImageLoader;
    private DeferredImageLoader mDeferredImageLoader;
    private boolean mDebuggable;

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
    public void load(@NonNull ImageRequest request, @NonNull View view, boolean background) {

        cancel(view);

        int viewWidth = view.getWidth();
        int viewHeight = view.getHeight();

        if (viewWidth > 0 && viewHeight > 0) {

            request.targetSize(
                    viewWidth - view.getPaddingLeft() - view.getPaddingRight(),
                    viewHeight - view.getPaddingTop() - view.getPaddingBottom()
            );

        }

        if (request.defer && (request.getTargetWidth() <= 0 || request.getTargetHeight() <= 0)) {
            mDeferredImageLoader.load(request, view, background);
        } else {

            // actual loading starts here

            // request transformer must come after target size determination
            if (request.transformer != null) {
                request = request.transformer.transform(request);
            }

            if (request.url == null) {
                setPlaceholder(request, view, background, null);
            } else {
                mMainImageLoader.load(request, view, background);
            }

        }

    }

    @Override
    public void load(@NonNull ImageRequest request, @NonNull DaliCallback callback) {

        cancel(callback);

        if (request.transformer != null) {
            request = request.transformer.transform(request);
        }

        if (request.url == null) {
            callback.onImageLoaded(
                    BitmapCompat.toBitmap(getApplicationContext(request.attachTarget), request.placeholderRes)
            );
        } else {
            mMainImageLoader.load(request, callback);
        }

    }

    public void setDebuggable(boolean debuggable) {
        mDebuggable = debuggable;
    }

    public boolean isDebuggable() {
        return mDebuggable;
    }

    @Override
    public void cancel(@NonNull Object target) {
        mDeferredImageLoader.cancel(target);
        mMainImageLoader.cancel(target);
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
