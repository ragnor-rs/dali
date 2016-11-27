package io.reist.dali.main;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import io.reist.dali.BuildConfig;
import io.reist.dali.Dali;
import io.reist.dali.DaliLoader;
import io.reist.dali.DeferredImageLoader;
import io.reist.dali.ShadowViewTreeObserver;
import io.reist.dali.SingleLoadingTest;
import io.reist.dali.TestImageView;

/**
 * Created by Reist on 10.06.16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(
        constants = BuildConfig.class,
        sdk = {Build.VERSION_CODES.JELLY_BEAN},
        shadows = ShadowViewTreeObserver.class
)
public class MainSingleLoadingTest extends SingleLoadingTest {

    public static final String TEST_URL_TRANSFORMED = TestImageTransformer.transformString(TEST_URL);

    @BeforeClass
    public static void init() {
        Dali.setMainImageLoaderClass(SyncTestImageLoader.class);
        Dali.setDeferredImageLoaderClass(TestDeferredImageLoader.class);
    }

    @Test
    public void testRequestTransformer() {

        Dali.with(RuntimeEnvironment.application)
                .load(TEST_URL)
                .defer(false)
                .transformer(new TestImageTransformer())
                .into(Mockito.mock(ImageView.class));

        SyncTestImageLoader imageLoader = ((SyncTestImageLoader) DaliLoader.getInstance().getMainImageLoader());

        Assert.assertEquals(TEST_URL_TRANSFORMED, imageLoader.getUrl());

    }

    @Test
    public void testMeasuredImageView() {
        TestActivity activity = createActivity();
        ViewGroup rootView = (ViewGroup) activity.findViewById(android.R.id.content);
        assertLoadingDeferred(rootView.getChildAt(0), false);
    }

    @Test
    public void testUnmeasuredImageView() {

        TestActivity activity = createActivity();

        TestImageView targetView = new TestImageView(activity);

        // image view is not yet measured - a request should be deferred
        assertLoadingDeferred(targetView, true);

        // measure the view - main image loader shouldn't trigger image draw
        targetView.assertMeasureDoesNotSetDrawable();

        // request image draw - main image loader should trigger setImageDrawable
        targetView.assertLayoutSetsDrawable();

    }

    static void assertLoadingDeferred(View targetView, boolean shouldCallDefer) {

        Dali.with(RuntimeEnvironment.application).load(TEST_URL).defer(true).into(targetView);

        DeferredImageLoader deferredImageLoader = DaliLoader.getInstance().getDeferredImageLoader();
        ((TestDeferredImageLoader) deferredImageLoader).assertLoadingDeferred(
                targetView,
                shouldCallDefer
        );

    }

}
