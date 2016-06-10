package io.reist.dali;

import android.graphics.Bitmap;

/**
 * Use in {@link Dali#setImageFactory(DaliImageFactory)} to change image loading mechanism
 *
 * Created by Reist on 10.06.16.
 */
public interface DaliImageFactory {

    /**
     * Synchronously loads a bitmap from the given url
     */
    Bitmap load(String url);

}
