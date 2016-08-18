package io.reist.dali;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import org.robolectric.internal.ShadowExtractor;
import org.robolectric.shadows.ShadowLooper;

/**
 * Created by Reist on 15.06.16.
 */
public class TestUtils {

    public static void delay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void advanceMainThread() {
        ShadowLooper.idleMainLooper();
        delay(1000);
    }

    public static String keyToUrl(int i) {
        return Integer.toString(i);
    }

    public static int urlToKey(String url) {
        return Integer.parseInt(url);
    }

    @NonNull
    public static Bitmap decode(int key) {
        Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565);
        Object shadow = ShadowExtractor.extract(bitmap);
        if (shadow instanceof TestShadowBitmap) {
            TestShadowBitmap shadowBitmap = (TestShadowBitmap) shadow;
            shadowBitmap.setActualKey(key);
        }
        System.out.println("decode(" + key + ")");
        return bitmap;
    }

}
