package io.reist.dali;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

/**
 * Created by Reist on 10.06.16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(
        constants = BuildConfig.class,
        sdk = {Build.VERSION_CODES.JELLY_BEAN},
        shadows = ShadowViewTreeObserver.class
)
public class SingleLoadingTest {

    public static final String TEST_URL = "test";
    public static final String TEST_URL_TRANSFORMED = TestImageTransformer.transformString(TEST_URL);

    @BeforeClass
    public static void init() {
        Dali.setMainImageLoaderClass(SyncTestImageLoader.class);
        Dali.setDeferredImageLoaderClass(TestDeferredImageLoader.class);
    }

    @Test
    public void testLoadIntoImageView() {
        ImageView imageView = Mockito.mock(ImageView.class);
        Dali.load(TEST_URL).defer(false).into(imageView);
        Mockito.verify(imageView).setImageDrawable(Mockito.any(Drawable.class));
    }

    @Test
    public void testLoadWithDaliCallback() {
        DaliCallback daliCallback = Mockito.mock(DaliCallback.class);
        Dali.load(TEST_URL).into(daliCallback, RuntimeEnvironment.application);
        Mockito.verify(daliCallback).onImageLoaded(Mockito.any(Bitmap.class));
    }

    @Test
    public void testMeasuredImageView() {
        TestActivity activity = Robolectric.setupActivity(TestActivity.class);
        ViewGroup rootView = (ViewGroup) activity.findViewById(android.R.id.content);
        assertLoadingDeferred(rootView.getChildAt(0), false);
    }

    @Test
    public void testUnmeasuredImageView() {

        TestActivity activity = Robolectric.setupActivity(TestActivity.class);

        TestImageView targetView = new TestImageView(activity);

        // image view is not yet measured - a request should be deferred
        assertLoadingDeferred(targetView, true);

        // measure the view - main image loader shouldn't trigger image draw
        targetView.assertMeasureDoesNotSetDrawable();

        // request image draw - main image loader should trigger setImageDrawable
        targetView.assertLayoutSetsDrawable();

    }

    @Test
    public void testRequestTransformer() {

        Dali.load(TEST_URL)
                .defer(false)
                .transformer(new TestImageTransformer())
                .into(Mockito.mock(ImageView.class));

        SyncTestImageLoader imageLoader = ((SyncTestImageLoader) Dali.getInstance().getMainImageLoader());

        Assert.assertEquals(TEST_URL_TRANSFORMED, imageLoader.getUrl());

    }

    public static void assertLoadingDeferred(View targetView, boolean shouldCallDefer) {

        Dali.load(TEST_URL).defer(true).into(targetView);

        DeferredImageLoader deferredImageLoader = Dali.getInstance().getDeferredImageLoader();
        ((TestDeferredImageLoader) deferredImageLoader).assertLoadingDeferred(
                targetView,
                shouldCallDefer
        );

    }

    static class TestActivity extends Activity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(new ImageView(this));
        }

    }

}
