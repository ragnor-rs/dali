package com.zvooq.dali;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by m039 on 12/30/15.
 */
public class ImageRequestBuilder {

    public String url;
    public int targetWidth;
    public int targetHeight;
    public ImageTransformer transformer = ImageTransformer.IDENTITY;
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

    public ImageRequestBuilder transformer(ImageTransformer transformer) {
        this.transformer = transformer;
        return this;
    }

    public ImageRequestBuilder centerCrop(boolean centerCrop) {
        this.centerCrop = centerCrop;
        return this;
    }

    /**
     * @param defer image loading will be deferred until an image be measured
     */
    public ImageRequestBuilder defer(boolean defer) {
        this.defer = defer;
        return this;
    }

    public ImageRequestBuilder inCircle(boolean inCircle) {
        this.inCircle = inCircle;
        return this;
    }

    public ImageRequestBuilder config(Bitmap.Config config) {
        this.config = config;
        return this;
    }

    public ImageRequestBuilder placeholder(@DrawableRes int placeholderRes) {
        this.placeholderRes = placeholderRes;
        return this;
    }

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

}
