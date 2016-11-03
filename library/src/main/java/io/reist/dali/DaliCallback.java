package io.reist.dali;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

/**
 * An object which uses images loaded by {@link Dali}. Basically, it's a view target, same as
 * {@link android.view.View}, but it doesn't have a background to load an image into.
 */
public interface DaliCallback {

    /**
     * Called when the bitmap is fully loaded. Please note that the bitmap may get recycled
     * after this method because of a particular {@link ImageLoader} implementation.
     * It is recommended to make a copy.
     */
    void onImageLoaded(@NonNull Bitmap bitmap);

}
