package io.reist.dali;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.BitmapTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.animation.NoAnimation;
import com.bumptech.glide.request.animation.ViewPropertyAnimation;
import com.bumptech.glide.request.target.BaseTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.ViewTarget;

import java.util.Map;
import java.util.WeakHashMap;

import io.reist.dali.drawables.CircleFadingBitmapDrawable;
import io.reist.dali.drawables.FadingBitmapDrawable;
import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * A loader which uses Glide library to asynchronously load images from the network.
 *
 * Glide bitmap recycling behaviours apply. See
 * https://github.com/bumptech/glide/wiki/Resource-re-use-in-Glide for details.
 */
public class GlideImageLoader implements ImageLoader {

    /**
     * This is to force Glide to generate dummy animations for non-cached images
     */
    private static final ViewPropertyAnimation.Animator sAnimator = new ViewPropertyAnimation.Animator() {

        @Override
        public void animate(View view) {}

    };

    public static final int BLUR_RADIUS = 8;
    public static final int BLUR_SAMPLING = 16;

    private final Map<Object, BaseTarget> targetMap = new WeakHashMap<>();

    @Override
    public void load(ImageRequestBuilder builder, View view, boolean background) {

        BitmapTypeRequest bitmapTypeRequest = createBitmapTypeRequest(
                view,
                builder,
                view.getContext().getApplicationContext()
        );

        bitmapTypeRequest.animate(sAnimator);

        enqueue(
                view,
                bitmapTypeRequest,
                new GlideImageLoaderViewTarget(
                        view,
                        builder.inCircle,
                        background,
                        this
                )
        );

    }

    private void enqueue(Object o, BitmapTypeRequest bitmapTypeRequest, BaseTarget<Bitmap> target) {
        targetMap.put(o, target);
        bitmapTypeRequest.into(target);
    }

    @NonNull
    private BitmapTypeRequest createBitmapTypeRequest(Object o, ImageRequestBuilder builder, Context context) {

        // cancel previous request
        cancel(o);

        // create a new one

        final RequestManager requestManager = Glide.with(context);

        BitmapTypeRequest bitmapTypeRequest = requestManager.load(builder.url).asBitmap();

        if (builder.placeholderRes != 0) {
            bitmapTypeRequest.placeholder(builder.placeholderRes);
        }

        if (builder.targetWidth > 0 && builder.targetHeight > 0) {
            bitmapTypeRequest.override(builder.targetWidth, builder.targetHeight);
        }

        if (builder.blur) {
            bitmapTypeRequest.transform(
                    new OnlyScaleDownTransformation(context, builder.centerCrop),
                    new BlurTransformation(context, BLUR_RADIUS, BLUR_SAMPLING)
            );
        } else {
            bitmapTypeRequest.transform(new OnlyScaleDownTransformation(context, builder.centerCrop));
        }

        if (builder.config != null) {
            bitmapTypeRequest.format(toGlideFormat(builder.config));
        }

        return bitmapTypeRequest;

    }

    @Override
    public void load(ImageRequestBuilder builder, DaliCallback callback, Context context) {
        enqueue(
                callback,
                createBitmapTypeRequest(callback, builder, context),
                new GlideImageLoaderCallbackTarget(callback, this)
        );
    }

    private static DecodeFormat toGlideFormat(Bitmap.Config config) {
        switch (config) {
            case RGB_565:
                return DecodeFormat.PREFER_RGB_565;
            case ARGB_8888:
                return DecodeFormat.PREFER_ARGB_8888;
            default:
                throw new IllegalArgumentException("Unsupported Bitmap config: " + config);
        }
    }

    @Override
    public void cancel(Object o) {
        if (o != null) {
            BaseTarget target = targetMap.get(o);
            if (target != null) {
                Glide.clear(target);
                targetMap.remove(o);
            }
        }
    }

    @Override
    public void cancelAll() {
        for (BaseTarget target : targetMap.values()) {
            Glide.clear(target);
        }
        targetMap.clear();
    }

    /**
     * The circle cropping has known issues with cross-fade transitions in Glide v3.
     * Details can be found on Glide GitHub page, section "Rounded images"
     * (https://github.com/bumptech/glide).
     *
     * TODO remove this class and replace with circleCrop() from Glide v4
     */
    private static class GlideImageLoaderViewTarget extends ViewTarget<View, Bitmap> {

        private final boolean inCircle;
        private final boolean background;
        private final GlideImageLoader loader;

        public GlideImageLoaderViewTarget(View view, boolean inCircle, boolean background, GlideImageLoader loader) {
            super(view);
            this.inCircle = inCircle;
            this.background = background;
            this.loader = loader;
        }

        @Override
        public void onLoadStarted(Drawable placeholder) {
            setDrawable(placeholder);
        }

        private void setDrawable(Drawable drawable) {
            if (background) {
                Dali.setBackground(view, drawable);
            } else {
                if (view instanceof ImageView) {
                    ((ImageView) view).setImageDrawable(drawable);
                } else {
                    throw new UnsupportedOperationException("Cannot set foreground for " + view);
                }
            }
        }

        private Drawable getDrawable() {
            if (background) {
                return view.getBackground();
            } else {
                if (view instanceof ImageView) {
                    return ((ImageView) view).getDrawable();
                } else {
                    throw new UnsupportedOperationException("Cannot set foreground for " + view);
                }
            }
        }

        @Override
        public void onLoadFailed(Exception e, Drawable errorDrawable) {
            setDrawable(errorDrawable);
            loader.targetMap.remove(this);
        }

        @Override
        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
            Drawable placeholder = getDrawable();
            final boolean noFade = glideAnimation == null || glideAnimation instanceof NoAnimation;
            if (inCircle) {
                setDrawable(new CircleFadingBitmapDrawable(view.getContext(), resource, placeholder, noFade));
            } else {
                setDrawable(new FadingBitmapDrawable(view.getContext(), resource, placeholder, noFade));
            }
            loader.targetMap.remove(this);
        }

    }

    private static class GlideImageLoaderCallbackTarget extends SimpleTarget<Bitmap> {

        private final DaliCallback callback;
        private final GlideImageLoader loader;

        public GlideImageLoaderCallbackTarget(DaliCallback callback, GlideImageLoader loader) {
            super();
            this.callback = callback;
            this.loader = loader;
        }

        @Override
        public void onLoadStarted(Drawable placeholder) {}

        @Override
        public void onLoadFailed(Exception e, Drawable errorDrawable) {
            loader.targetMap.remove(this);
        }

        @Override
        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
            callback.onImageLoaded(resource);
            loader.targetMap.remove(this);
        }

    }

    /**
     * There's no equivalent for Picasso's onlyScaleDown in Glide. To achieve the same effect,
     * here goes a transformation with the same effect.
     */
    private static class OnlyScaleDownTransformation extends CenterCrop {

        public static final String ID = OnlyScaleDownTransformation.class.getName();

        private final boolean centerCrop;

        public OnlyScaleDownTransformation(Context context, boolean centerCrop) {
            super(context);
            this.centerCrop = centerCrop;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {

            if (centerCrop) {
                float scale;
                if (toTransform.getWidth() * outHeight > outWidth * toTransform.getHeight()) {
                    scale = (float) outHeight / (float) toTransform.getHeight();
                } else {
                    scale = (float) outWidth / (float) toTransform.getWidth();
                }
                if (scale < 1f) {
                    return super.transform(pool, toTransform, outWidth, outHeight);
                }
            }

            final Bitmap toReuse = pool.get(
                    toTransform.getWidth(),
                    toTransform.getHeight(),
                    toTransform.getConfig() != null ? toTransform.getConfig() : Bitmap.Config.ARGB_8888
            );

            if (toReuse != null) {
                final Paint paint = new Paint(TransformationUtils.PAINT_FLAGS);
                final Canvas canvas = new Canvas(toReuse);
                canvas.drawBitmap(toTransform, 0, 0, paint);
                return toReuse;
            } else {
                return Bitmap.createBitmap(toTransform);
            }

        }

        @Override
        public String getId() {
            return ID + "(centerCrop = " + centerCrop + ")";
        }

    }

}
