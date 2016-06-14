package io.reist.dali;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Builds requests for {@link Dali}.
 *
 * Created by m039 on 12/30/15.
 */
public class ImageRequestBuilder {

    public String url;
    public int targetWidth;
    public int targetHeight;
    public ImageRequestTransformer transformer = ImageRequestTransformer.IDENTITY;
    public boolean centerCrop = true;
    public boolean defer = true;
    public boolean inCircle = false;
    public Bitmap.Config config;
    public @DrawableRes int placeholderRes;
    public boolean blur = false;

    ImageRequestBuilder() {}

    public ImageRequestBuilder url(String url) {
        this.url = url;
        return this;
    }

    public ImageRequestBuilder resize(int width, int height) {
        this.targetWidth = width;
        this.targetHeight = height;
        return this;
    }

    @SuppressWarnings("unused")
    public ImageRequestBuilder transformer(ImageRequestTransformer transformer) {
        this.transformer = transformer;
        return this;
    }

    @SuppressWarnings("unused")
    public ImageRequestBuilder centerCrop(boolean centerCrop) {
        this.centerCrop = centerCrop;
        return this;
    }

    /**
     * @param defer     image loading will be deferred until an image be measured
     */
    @SuppressWarnings("unused")
    public ImageRequestBuilder defer(boolean defer) {
        this.defer = defer;
        return this;
    }

    @SuppressWarnings("unused")
    public ImageRequestBuilder inCircle(boolean inCircle) {
        this.inCircle = inCircle;
        return this;
    }

    @SuppressWarnings("unused")
    public ImageRequestBuilder config(Bitmap.Config config) {
        this.config = config;
        return this;
    }

    @SuppressWarnings("unused")
    public ImageRequestBuilder placeholder(@DrawableRes int placeholderRes) {
        this.placeholderRes = placeholderRes;
        return this;
    }

    @SuppressWarnings("unused")
    public ImageRequestBuilder blur(boolean blur) {
        this.blur = blur;
        return this;
    }

    public void into(@NonNull View view) {
        into(view, false);
    }

    public void into(@NonNull View view, boolean background) {
        Dali.getInstance().load(this, view, background);
    }

    public void into(@NonNull DaliCallback callback, @NonNull Context context) {
        Dali.getInstance().load(this, callback, context);
    }

    public ImageRequestBuilder targetSize(int w, int h) {
        targetWidth = w;
        targetHeight = h;
        return this;
    }

}
