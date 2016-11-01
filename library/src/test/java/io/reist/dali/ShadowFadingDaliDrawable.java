package io.reist.dali;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.robolectric.annotation.Implements;
import org.robolectric.internal.ShadowExtractor;
import org.robolectric.shadows.ShadowDrawable;

import io.reist.dali.TestShadowBitmap;
import io.reist.dali.drawables.FadingDaliDrawable;

/**
 * Created by Reist on 01.11.16.
 */

@Implements(FadingDaliDrawable.class)
public class ShadowFadingDaliDrawable extends ShadowDrawable {

    private int key;

    public void __constructor__(
            @NonNull Bitmap bitmap,
            @Nullable Drawable placeholder,
            @Nullable Bitmap.Config placeholderConfig
    ) {
        Object shadow = ShadowExtractor.extract(bitmap);
        if (shadow instanceof TestShadowBitmap) {
            TestShadowBitmap shadowBitmap = (TestShadowBitmap) shadow;
            key = shadowBitmap.getActualKey();
        }
    }

    public int getKey() {
        return key;
    }

}
