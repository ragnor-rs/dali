package io.reist.dali;

import org.robolectric.shadows.ShadowLooper;

/**
 * Created by Reist on 15.06.16.
 */
public class TestUtils {

    public static void delay(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void advanceMainThread() {
        ShadowLooper.idleMainLooper();
        delay(1);
    }

    public static String keyToUrl(int i) {
        return Integer.toString(i);
    }

    public static int urlToKey(String url) {
        return Integer.parseInt(url);
    }

}
