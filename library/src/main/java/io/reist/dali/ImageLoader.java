package io.reist.dali;

import android.content.Context;
import android.view.View;

/**
 * A image loader which is able to load images asynchronously into {@link View}s and
 * {@link DaliCallback}.
 *
 * Created by m039 on 12/30/15.
 */
interface ImageLoader {

    /**
     * @param builder       a request to use
     * @param view          a target view
     * @param background    set a loaded image as a background
     */
    void load(ImageRequestBuilder builder, View view, boolean background);

    /**
     * @param builder       a request to use
     * @param callback      a callback to call on image load completion
     * @param context       Android application context
     */
    void load(ImageRequestBuilder builder, DaliCallback callback, Context context);

    /**
     * Interrupts an image-loading request. To interrupt a request, pass a {@link View} or
     * a {@link DaliCallback} which were used by {@link #load(ImageRequestBuilder, View, boolean)}
     * or by {@link #load(ImageRequestBuilder, DaliCallback, Context)} respectively.
     */
    void cancel(Object o);

}
