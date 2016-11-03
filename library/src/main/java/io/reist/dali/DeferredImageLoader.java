package io.reist.dali;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

import static io.reist.dali.DaliUtils.setPlaceholder;

/**
 *
 * DeferredImageLoader doesn't perform actual image loading. It just postpones image request until
 * a view is measured to take advantage of optimizations of various Dali implementations. For
 * instance, if a view size is known and this size is relatively small, it's possible to use less
 * memory by storing only scaled-down instances of images.
 *
 * Subclasses must have a public constructor with {@link ImageLoader} parameter.
 *
 * Created by m039 on 12/25/15.
 */
public class DeferredImageLoader implements ImageLoader {

    /**
     * It's ok that the map is never queried because the loading process starts in
     * {@link ViewRequestFactory#ViewRequestFactory(View, ImageRequestBuilder, boolean, ImageLoader)}.
     * In here, a pre-draw listener is created. The listener requests an image after the
     * attached {@link ImageView} size has its size calculated.
     */
    private final Map<View, ViewRequestFactory> requestMap = new WeakHashMap<>();

    protected static class ViewRequestFactory implements ViewTreeObserver.OnPreDrawListener {

        private final ImageRequestBuilder builder;
        private final WeakReference<View> target;
        private final boolean background;
        private final ImageLoader mainImageLoader;

        ViewRequestFactory(
                View target,
                ImageRequestBuilder builder,
                boolean background,
                ImageLoader mainImageLoader
        ) {

            this.builder = builder;
            this.target = new WeakReference<>(target);
            this.background = background;
            this.mainImageLoader = mainImageLoader;

            target.getViewTreeObserver().addOnPreDrawListener(this);

        }

        @Override
        public boolean onPreDraw() {

            View target = this.target.get();

            if (target == null) {
                return true;
            }

            ViewTreeObserver vto = target.getViewTreeObserver();

            if (!vto.isAlive()) {
                return true;
            }

            int width = target.getWidth();
            int height = target.getHeight();

            if (width <= 0 || height <= 0) {
                return true;
            }

            mainImageLoader.load(builder, target, background);

            vto.removeOnPreDrawListener(this);

            return true;

        }

        void cancel() {

            View target = this.target.get();

            if (target == null) {
                return;
            }

            ViewTreeObserver vto = target.getViewTreeObserver();

            if (!vto.isAlive()) {
                return;
            }

            vto.removeOnPreDrawListener(this);

        }

    }

    public void defer(View view, ViewRequestFactory viewRequestFactory) {
        requestMap.put(view, viewRequestFactory);
    }

    @Override
    public void load(@NonNull ImageRequestBuilder builder, @NonNull View view, boolean background) {

        setPlaceholder(builder, view, background);

        int width, height;

        width = view.getWidth();
        height = view.getHeight();

        if (width <= 0 || height <= 0) {
            defer(view, new ViewRequestFactory(view, builder, background, DaliLoader.getInstance().getMainImageLoader()));
        } else {
            builder.resize(width, height).into(view, background);
        }

    }

    @Override
    public void load(@NonNull ImageRequestBuilder builder, @NonNull DaliCallback callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void cancel(@NonNull Object o) {
        if (o instanceof View) {
            View view = (View) o;
            ViewRequestFactory viewRequestFactory = requestMap.remove(view);
            if (viewRequestFactory != null) {
                viewRequestFactory.cancel();
            }
        }
    }

    @Override
    public void cancelAll() {
        for (ViewRequestFactory factory : requestMap.values()) {
            factory.cancel();
        }
        requestMap.clear();
    }

}
