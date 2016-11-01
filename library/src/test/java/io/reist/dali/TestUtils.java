package io.reist.dali;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.os.SystemClock;
import android.support.annotation.NonNull;

import org.junit.Assert;
import org.mockito.Mockito;
import org.robolectric.internal.ShadowExtractor;
import org.robolectric.shadows.ShadowLooper;

import io.reist.dali.drawables.DaliDrawable;
import io.reist.dali.drawables.FadingDaliDrawable;

import static junit.framework.Assert.*;

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

    protected static void assertDrawable(DaliDrawable daliDrawable) {

        Assert.assertEquals(255, daliDrawable.getAlpha());
        Assert.assertEquals(PixelFormat.OPAQUE, daliDrawable.getOpacity());

        daliDrawable.setAlpha(127);
        Assert.assertEquals(127, daliDrawable.getAlpha());
        Assert.assertEquals(PixelFormat.TRANSLUCENT, daliDrawable.getOpacity());

        daliDrawable.setAlpha(0);
        Assert.assertEquals(PixelFormat.TRANSPARENT, daliDrawable.getOpacity());

    }

    protected static void checkFadingDrawable(FadingDaliDrawable daliDrawable) throws InterruptedException {

        assertDrawable(daliDrawable);

        assertTrue(daliDrawable.isFadingIn());

        Canvas canvas = Mockito.mock(Canvas.class);

        long startTime = SystemClock.uptimeMillis();

        daliDrawable.draw(canvas);
        assertTrue(startTime <= daliDrawable.getStartTime());

        Thread.sleep((long) (FadingDaliDrawable.FADE_DURATION / 2f));
        daliDrawable.draw(canvas);
        assertTrue(0f < daliDrawable.getProgress() && daliDrawable.getProgress() < 1f);

        Thread.sleep((long) (FadingDaliDrawable.FADE_DURATION / 2f));
        daliDrawable.draw(canvas);
        assertEquals(1f, daliDrawable.getProgress());

    }

}
