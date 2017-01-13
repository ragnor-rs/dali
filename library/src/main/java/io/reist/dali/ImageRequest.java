/*
 * Copyright (C) 2017 Renat Sarymsakov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.reist.dali;

import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Requests for {@link Dali}.
 *
 * Created by m039 on 12/30/15.
 */
@SuppressWarnings("WeakerAccess")
public class ImageRequest {

    public final Object attachTarget;

    public String url = null;
    public ImageRequestTransformer transformer = ImageRequestTransformer.IDENTITY;
    public boolean defer = true;
    public boolean inCircle = false;
    public Bitmap.Config config = Bitmap.Config.ARGB_8888;
    public @DrawableRes int placeholderRes;
    public boolean blur = false;
    public boolean disableTransformation = false;
    public ScaleMode scaleMode = ScaleMode.CENTER_INSIDE;

    private int targetWidth = 0;
    private int targetHeight = 0;

    public ImageRequest() {
        attachTarget = null;
    }

    public ImageRequest(@NonNull Object attachTarget) {
        this.attachTarget = attachTarget;
    }

    public ImageRequest url(String url) {
        this.url = url;
        return this;
    }

    @SuppressWarnings("unused")
    public ImageRequest transformer(ImageRequestTransformer transformer) {
        this.transformer = transformer;
        return this;
    }

    @SuppressWarnings("unused")
    public ImageRequest scaleMode(ScaleMode scaleMode) {
        this.scaleMode = scaleMode;
        return this;
    }

    /**
     * @param defer     image loading will be deferred until an image be measured
     */
    @SuppressWarnings("unused")
    public ImageRequest defer(boolean defer) {
        this.defer = defer;
        return this;
    }

    @SuppressWarnings("unused")
    public ImageRequest inCircle(boolean inCircle) {
        this.inCircle = inCircle;
        return this;
    }

    @SuppressWarnings("unused")
    public ImageRequest config(Bitmap.Config config) {
        this.config = config;
        return this;
    }

    @SuppressWarnings("unused")
    public ImageRequest placeholder(@DrawableRes int placeholderRes) {
        this.placeholderRes = placeholderRes;
        return this;
    }

    @SuppressWarnings("unused")
    public ImageRequest blur(boolean blur) {
        this.blur = blur;
        return this;
    }

    public void into(@NonNull View view) {
        into(view, false);
    }

    public void into(@NonNull View view, boolean background) {
        DaliLoader.getInstance().load(this, view, background);
    }

    public void into(@NonNull DaliCallback callback) {
        DaliLoader.getInstance().load(this, callback);
    }

    public ImageRequest targetSize(int w, int h) {
        targetWidth = w;
        targetHeight = h;
        return this;
    }

    @SuppressWarnings("unused")
    public ImageRequest disableTransformation(boolean disableTransformation) {
        this.disableTransformation = disableTransformation;
        return this;
    }

    public int getTargetWidth() {
        return targetWidth;
    }

    public int getTargetHeight() {
        return targetHeight;
    }

}
