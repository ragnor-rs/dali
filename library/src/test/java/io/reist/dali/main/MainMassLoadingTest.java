package io.reist.dali.main;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.reist.dali.BuildConfig;
import io.reist.dali.Dali;
import io.reist.dali.DeferredImageLoader;
import io.reist.dali.TestImageView;
import io.reist.dali.TestUtils;
import io.reist.dali.ViewRecycler;

/**
 * Created by Reist on 14.06.16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(
        constants = BuildConfig.class,
        sdk = {Build.VERSION_CODES.JELLY_BEAN}
)
public class MainMassLoadingTest {

    public static final int WINDOW_HEIGHT = 5;
    public static final int DATA_SET_LENGTH = 100;

    @BeforeClass
    public static void init() {
        Dali.setMainImageLoaderClass(AsyncTestImageLoader.class);
        Dali.setDeferredImageLoaderClass(DeferredImageLoader.class);
    }

    @Test
    public void perform() {

        TestActivity testActivity = Robolectric.setupActivity(TestActivity.class);

        final ViewRecycler<TestImageView> finalRecycler = testActivity.recycler;

        for (int i = 0; i < 2; i++) {

            final int finalPos = i * WINDOW_HEIGHT;
            RunnableQueue.getInstance().post(new Runnable() {

                @Override
                public void run() {
                    finalRecycler.setPosition(finalPos);
                    finalRecycler.render();
                }

            });

            TestUtils.delay(1);

        }

        TestUtils.delay(10);

        Assert.assertEquals(
                "Out of sync",
                testActivity.getTotal(),
                testActivity.getSuccessful()
        );

        assertVisibleImagesLoaded(testActivity, finalRecycler);

    }

    public static void assertVisibleImagesLoaded(
            TestActivity testActivity,
            ViewRecycler<TestImageView> finalRecycler
    ) {

        int lastPosition = finalRecycler.getPosition();
        int windowBottom = Math.min(
                finalRecycler.getAdapter().getCount(),
                lastPosition + finalRecycler.getWindowHeight()
        );

        for (int i = lastPosition; i < windowBottom; i++) {
            Assert.assertTrue("Not loaded image for " + i, testActivity.loadedKeys.contains(i));
        }

    }

    static class TestActivity extends Activity implements TestImageView.Callback {

        /**
         * A number of successful image loads. A successful load results in a loaded image
         * corresponding to the item. The correspondence is verified via
         * {@link TestImageView#expectedKey} and {@link TestImageView#actualKey}.
         */
        private volatile int successful;

        private ViewRecycler<TestImageView> recycler;

        /**
         * Loaded positions. This is used to check if each visible position has been loaded after
         * all scrolling manipulations via {@link ViewRecycler#setPosition(int)} and
         * {@link ViewRecycler#render()}.
         */
        private final List<Integer> loadedKeys = new CopyOnWriteArrayList<>();

        @Override
        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);

            recycler = new ViewRecycler<>(WINDOW_HEIGHT, new ViewRecycler.Adapter<TestImageView>() {

                @Override
                public int getCount() {
                    return DATA_SET_LENGTH;
                }

                @Override
                public TestImageView createView(int i) {
                    return new TestImageView(TestActivity.this, TestActivity.this);
                }

                @Override
                public void bindView(TestImageView testImageView, int i) {
                    testImageView.setExpectedKey(i);
                    Dali.load(TestUtils.keyToUrl(i))
                            .placeholder(android.R.color.black)
                            .targetSize(1, 1)
                            .into(testImageView);
                }

            });

        }

        public int getTotal() {
            return loadedKeys.size();
        }

        public int getSuccessful() {
            return successful;
        }

        @Override
        public void onSetImageDrawable(int expectedKey, int actualKey) {
            System.out.println("onSetImageDrawable(" + expectedKey + ", " + actualKey + ")");
            loadedKeys.add(actualKey);
            if (actualKey == expectedKey) {
                successful++;
            }
        }

    }

}
