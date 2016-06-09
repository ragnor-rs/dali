package io.reist.dali;

import android.content.Context;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by m039 on 12/25/15.
 */
class DeferredImageLoader implements ImageLoader {

    DeferredImageLoader() {}

    /**
     * It's ok that the map is never queried because the loading process starts in
     * {@link DeferredRequestCreator#DeferredRequestCreator(View, ImageRequestBuilder, boolean)}.
     * In here, a pre-draw listener is created. The listener requests an image after the
     * attached {@link ImageView} size has its size calculated.
     */
    private static final Map<View, DeferredRequestCreator> sTargetToDeferredRequestCreator = new WeakHashMap<>();

    private static class DeferredRequestCreator implements ViewTreeObserver.OnPreDrawListener {

        private final ImageRequestBuilder builder;
        private final WeakReference<View> target;
        private final boolean background;

        DeferredRequestCreator(View target, ImageRequestBuilder builder, boolean background) {
            this.builder = builder;
            this.target = new WeakReference<>(target);
            this.background = background;
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

            vto.removeOnPreDrawListener(this);

            this.builder.defer(false).resize(width, height).into(target, background);

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

    private static void defer(View view, DeferredRequestCreator deferredRequestCreator) {
        sTargetToDeferredRequestCreator.put(view, deferredRequestCreator);
    }

    @Override
    public void load(ImageRequestBuilder builder, View view, boolean background) {

        if (builder.placeholderRes != 0) {
            Dali.setPlaceholder(builder.placeholderRes, view, background);
        }

        int width, height;

        width = view.getWidth();
        height = view.getHeight();

        if (width <= 0 || height <= 0) {
            defer(view, new DeferredRequestCreator(view, builder, background));
        } else {
            builder.resize(width, height).into(view, background);
        }

    }

    @Override
    public void load(ImageRequestBuilder builder, DaliCallback callback, Context context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void cancel(Object o) {
        if (o instanceof View) {
            View view = (View) o;
            DeferredRequestCreator deferredRequestCreator = sTargetToDeferredRequestCreator.remove(view);
            if (deferredRequestCreator != null) {
                deferredRequestCreator.cancel();
            }
        }
    }

}
