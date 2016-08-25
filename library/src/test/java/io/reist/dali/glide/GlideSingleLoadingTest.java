package io.reist.dali.glide;

import android.net.Network;
import android.os.Build;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implements;

import io.reist.dali.BuildConfig;
import io.reist.dali.Dali;
import io.reist.dali.DeferredImageLoader;
import io.reist.dali.GlideImageLoader;
import io.reist.dali.RobolectricGradle3TestRunner;
import io.reist.dali.SingleLoadingTest;

/**
 * Created by Reist on 10.06.16.
 */
@RunWith(RobolectricGradle3TestRunner.class)
@Config(
        constants = BuildConfig.class,
        sdk = {Build.VERSION_CODES.JELLY_BEAN},
        shadows = {
                GlideSingleLoadingTest.ShadowNetwork.class,
                GlideShadowStreamBitmapDecoder.class
        },
        application = GlideTestApp.class
)
public class GlideSingleLoadingTest extends SingleLoadingTest {

    @BeforeClass
    public static void init() {
        Dali.setMainImageLoaderClass(GlideImageLoader.class);
        Dali.setDeferredImageLoaderClass(DeferredImageLoader.class);
    }

    @Implements(Network.class)
    public class ShadowNetwork {}

}
