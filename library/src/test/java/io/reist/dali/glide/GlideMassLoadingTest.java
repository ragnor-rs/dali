package io.reist.dali.glide;

import android.os.Build;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import io.reist.dali.BuildConfig;
import io.reist.dali.Dali;
import io.reist.dali.DeferredImageLoader;
import io.reist.dali.MassLoadingTest;
import io.reist.dali.ShadowFadingDaliDrawable;
import io.reist.dali.TestShadowBitmap;

/**
 * Created by Reist on 14.06.16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(
        constants = BuildConfig.class,
        sdk = {Build.VERSION_CODES.JELLY_BEAN},
        shadows = {
                GlideSingleLoadingTest.ShadowNetwork.class,
                GlideShadowStreamBitmapDecoder.class,
                TestShadowBitmap.class,
                ShadowFadingDaliDrawable.class
        },
        application = GlideTestApp.class
)
public class GlideMassLoadingTest extends MassLoadingTest {

    @BeforeClass
    public static void init() {
        Dali.setMainImageLoaderClass(GlideImageLoader.class);
        Dali.setDeferredImageLoaderClass(DeferredImageLoader.class);
    }

}
