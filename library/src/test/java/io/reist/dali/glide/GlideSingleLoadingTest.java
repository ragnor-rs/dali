package io.reist.dali.glide;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Network;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowLooper;

import io.reist.dali.BuildConfig;
import io.reist.dali.Dali;
import io.reist.dali.DaliCallback;
import io.reist.dali.DeferredImageLoader;
import io.reist.dali.GlideImageLoader;
import io.reist.dali.TestImageView;
import io.reist.dali.TestUtils;

/**
 * Created by Reist on 10.06.16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(
        constants = BuildConfig.class,
        sdk = {Build.VERSION_CODES.JELLY_BEAN},
        shadows = {
                GlideSingleLoadingTest.ShadowNetwork.class,
                GlideShadowStreamBitmapDecoder.class
        },
        application = GlideTestApp.class
)
public class GlideSingleLoadingTest {

    public static final String TEST_URL = "test";

    @BeforeClass
    public static void init() {
        Dali.setMainImageLoaderClass(GlideImageLoader.class);
        Dali.setDeferredImageLoaderClass(DeferredImageLoader.class);
    }

    @Test
    public void testLoadIntoImageView() {

        TestActivity activity = Robolectric.setupActivity(TestActivity.class);

        ViewGroup rootView = (ViewGroup) activity.findViewById(android.R.id.content);
        TestImageView view = (TestImageView) rootView.getChildAt(0);

        // load and wait until the request is executed and the result is posted to the main thread
        Dali.load(TEST_URL).defer(false).into(view);
        TestUtils.delay(3);

        // process pending results - set the drawable
        ShadowLooper.idleMainLooper();
        TestUtils.delay(3);

        view.assertSetDrawableCalled();

    }

    @Test
    public void testLoadWithDaliCallback() {

        DaliCallback daliCallback = Mockito.mock(DaliCallback.class);

        // load and wait until the request is executed and the result is posted to the main thread
        Dali.load(TEST_URL).into(daliCallback, RuntimeEnvironment.application);
        TestUtils.delay(3);

        // process pending results - set the drawable
        ShadowLooper.idleMainLooper();
        TestUtils.delay(3);

        Mockito.verify(daliCallback).onImageLoaded(Mockito.any(Bitmap.class));

    }

    static class TestActivity extends Activity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(new TestImageView(this));
        }

    }

    @Implements(Network.class)
    public class ShadowNetwork {}

}
