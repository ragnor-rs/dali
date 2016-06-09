package com.zvooq.dali;

import android.content.Context;
import android.view.View;

/**
 * Created by m039 on 12/30/15.
 */
interface ImageLoader {

    void load(ImageRequestBuilder builder, View view, boolean background);
    void load(ImageRequestBuilder builder, DaliCallback callback, Context context);

    /**
     * Interrupts an image-loading request. To interrupt a request, pass a {@link View} or
     * a {@link DaliCallback} which were used by {@link #load(ImageRequestBuilder, View, boolean)}
     * or by {@link #load(ImageRequestBuilder, DaliCallback, Context)} respectively.
     */
    void cancel(Object o);

}
