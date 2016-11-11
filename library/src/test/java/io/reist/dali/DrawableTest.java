package io.reist.dali;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import io.reist.dali.drawables.CircleFadingDaliDrawable;
import io.reist.dali.drawables.DaliDrawable;
import io.reist.dali.drawables.FadingDaliDrawable;

import static io.reist.dali.TestUtils.assertDrawable;
import static io.reist.dali.TestUtils.checkFadingDrawable;

/**
 * Created by Reist on 01.11.16.
 */

@RunWith(RobolectricGradle3TestRunner.class)
@Config(
        constants = BuildConfig.class,
        sdk = {Build.VERSION_CODES.JELLY_BEAN},
        shadows = ShadowViewTreeObserver.class
)
public class DrawableTest {

    @Test
    public void daliDrawable() {

        DaliDrawable daliDrawable = new DaliDrawable(
                Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888),
                ScaleMode.CENTER_INSIDE,
                1,
                1
        );
        assertDrawable(daliDrawable);

        // TODO check correctness of drawable size

    }

    @Test
    public void fadingDaliDrawable() throws InterruptedException {

        FadingDaliDrawable daliDrawable = new FadingDaliDrawable(
                Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888),
                ScaleMode.CENTER_INSIDE,
                1,
                1,
                new BitmapDrawable(
                        RuntimeEnvironment.application.getResources(),
                        Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                ),
                null,
                false
        );
        checkFadingDrawable(daliDrawable);

        // TODO check correctness of drawable size

    }

    @Test
    public void circleFadingDaliDrawable() throws InterruptedException {

        CircleFadingDaliDrawable daliDrawable = new CircleFadingDaliDrawable(
                Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888),
                ScaleMode.CENTER_INSIDE,
                1,
                1,
                new BitmapDrawable(
                        RuntimeEnvironment.application.getResources(),
                        Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                ),
                null,
                false
        );
        checkFadingDrawable(daliDrawable);

        // TODO check correctness of drawable size

    }

}


