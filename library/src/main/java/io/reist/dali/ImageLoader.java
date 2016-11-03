package io.reist.dali;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * A image loader which is able to load images asynchronously into {@link View}s and
 * {@link DaliCallback}.
 *
 * Created by m039 on 12/30/15.
 */
public interface ImageLoader {

    /**
     * @param builder       a request to use
     * @param view          a target view
     * @param background    set a loaded image as a background
     */
    void load(@NonNull ImageRequestBuilder builder, @NonNull View view, boolean background);

    /**
     * @param builder       a request to use
     * @param callback      a callback to call on image load completion
     */
    void load(@NonNull ImageRequestBuilder builder, @NonNull DaliCallback callback);

    /**
     * Interrupts an image-loading request. To interrupt a request, pass a {@link View} or
     * a {@link DaliCallback} which were used by {@link #load(ImageRequestBuilder, View, boolean)}
     * or by {@link #load(ImageRequestBuilder, DaliCallback)} respectively.
     */
    void cancel(@NonNull Object o);

    /**
     * Stops all requests
     */
    void cancelAll();

}
