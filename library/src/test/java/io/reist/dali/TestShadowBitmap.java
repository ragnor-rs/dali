package io.reist.dali;

import android.graphics.Bitmap;

import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowBitmap;

/**
 * Created by Reist on 15.06.16.
 */
@Implements(Bitmap.class)
public class TestShadowBitmap extends ShadowBitmap {

    private int actualKey;

    public int getActualKey() {
        return actualKey;
    }

    public void setActualKey(int actualKey) {
        this.actualKey = actualKey;
    }

}
