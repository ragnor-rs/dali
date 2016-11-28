package io.reist.dali.main;

import android.os.Build;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import io.reist.dali.BuildConfig;
import io.reist.dali.Dali;
import io.reist.dali.DeferredImageLoader;
import io.reist.dali.MassLoadingTest;
import io.reist.dali.TestShadowBitmap;

/**
 * Created by Reist on 14.06.16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(
        constants = BuildConfig.class,
        sdk = {Build.VERSION_CODES.JELLY_BEAN},
        shadows = TestShadowBitmap.class
)
public class MainMassLoadingTest extends MassLoadingTest {

    @BeforeClass
    public static void init() {
        Dali.setMainImageLoaderClass(AsyncTestImageLoader.class);
        Dali.setDeferredImageLoaderClass(DeferredImageLoader.class);
    }

}
