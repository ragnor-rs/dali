package io.reist.dali;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.internal.ShadowExtractor;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Reist on 10.06.16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(
        constants = BuildConfig.class,
        sdk = {Build.VERSION_CODES.JELLY_BEAN},
        shadows = SingleLoadingTest.PreDrawShadowViewTreeObserver.class
)
public class SingleLoadingTest {

    public static final String TEST_URL = "test";
    public static final String TEST_URL_TRANSFORMED = transformString(TEST_URL);

    @BeforeClass
    public static void init() {
        Dali.setMainImageLoaderClass(TestImageLoader.class);
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
        assertDefer(rootView.getChildAt(0), false);
    }

    @Test
    public void testUnmeasuredImageView() {

        TestActivity activity = Robolectric.setupActivity(TestActivity.class);

        TestImageView targetView = new TestImageView(activity);

        // image view is not yet measured - a request should be deferred
        assertDefer(targetView, true);

        // measure the view - main image loader shouldn't trigger image draw
        targetView.measure(
                View.MeasureSpec.makeMeasureSpec(4, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(3, View.MeasureSpec.EXACTLY)
        );
        Mockito.verify(targetView.dummy, Mockito.times(0)).setImageDrawable(Mockito.any(Drawable.class));

        // request image draw - main image loader should trigger setImageDrawable
        targetView.layout(0, 0, 100, 100);
        PreDrawShadowViewTreeObserver viewTreeObserver =
                (PreDrawShadowViewTreeObserver) ShadowExtractor.extract(targetView.getViewTreeObserver());
        viewTreeObserver.fireOnPreDrawListeners();
        Mockito.verify(targetView.dummy, Mockito.times(1)).setImageDrawable(Mockito.any(Drawable.class));

    }

    @Test
    public synchronized void testRequestTransformer() {

        Dali.load(TEST_URL)
                .defer(false)
                .transformer(new TestImageTransformer())
                .into(Mockito.mock(ImageView.class));

        TestImageLoader imageLoader = ((TestImageLoader) Dali.getInstance().getMainImageLoader());

        Assert.assertEquals(TEST_URL_TRANSFORMED, imageLoader.getUrl());

    }

    private static void assertDefer(View targetView, boolean shouldCall) {

        Dali.load(TEST_URL).defer(true).into(targetView);

        Mockito.verify(
                ((TestDeferredImageLoader) Dali.getInstance().getDeferredImageLoader()).dummy,
                Mockito.times(shouldCall ? 1 : 0)
        ).defer(
                Mockito.eq(targetView),
                Mockito.any(DeferredImageLoader.ViewRequestFactory.class)
        );

    }

    static class TestImageLoader implements ImageLoader {

        private String url;

        @Override
        public void load(ImageRequestBuilder builder, View view, boolean background) {
            this.url = builder.url;
            if (view instanceof ImageView) {
                ((ImageView) view).setImageDrawable(Mockito.mock(Drawable.class));
            } else {
                throw new UnsupportedOperationException();
            }
        }

        @Override
        public void load(ImageRequestBuilder builder, DaliCallback callback, Context context) {
            this.url = builder.url;
            callback.onImageLoaded(Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565));
        }

        @Override
        public void cancel(Object o) {}

        @Override
        public void cancelAll() {
            throw new UnsupportedOperationException();
        }

        public String getUrl() {
            return url;
        }

    }

    static class TestActivity extends Activity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(new ImageView(this));
        }

    }

    static class TestDeferredImageLoader extends DeferredImageLoader {

        private final DeferredImageLoader dummy = Mockito.mock(DeferredImageLoader.class);

        @Override
        protected void defer(View view, DeferredImageLoader.ViewRequestFactory viewRequestFactory) {
            super.defer(view, viewRequestFactory);
            dummy.defer(view, viewRequestFactory);
        }

    }

    /**
     * Taken from
     * https://github.com/bumptech/glide/blob/3c3bcc21a0e8adf596bc9f714286943565aab719/library/src/test/java/com/bumptech/glide/request/target/ViewTargetTest.java#L406
     */
    @Implements(ViewTreeObserver.class)
    public static class PreDrawShadowViewTreeObserver {

        private final CopyOnWriteArrayList<ViewTreeObserver.OnPreDrawListener> preDrawListeners = new CopyOnWriteArrayList<>();

        private boolean isAlive = true;

        @SuppressWarnings("unused")
        @Implementation
        public void addOnPreDrawListener(ViewTreeObserver.OnPreDrawListener listener) {
            checkIsAlive();
            preDrawListeners.add(listener);
        }

        @SuppressWarnings("unused")
        @Implementation
        public void removeOnPreDrawListener(ViewTreeObserver.OnPreDrawListener listener) {
            checkIsAlive();
            preDrawListeners.remove(listener);
        }

        @Implementation
        public boolean isAlive() {
            return isAlive;
        }

        private void checkIsAlive() {
            if (!isAlive()) {
                throw new IllegalStateException("ViewTreeObserver is not alive!");
            }
        }

        @SuppressWarnings("unused")
        public void setIsAlive(boolean isAlive) {
            this.isAlive = isAlive;
        }

        @SuppressWarnings("unused")
        public void fireOnPreDrawListeners() {
            for (ViewTreeObserver.OnPreDrawListener listener : preDrawListeners) {
                listener.onPreDraw();
            }
        }

        @SuppressWarnings("unused")
        public List<ViewTreeObserver.OnPreDrawListener> getPreDrawListeners() {
            return preDrawListeners;
        }

    }

    static class TestImageView extends ImageView {

        private final ImageView dummy = Mockito.mock(ImageView.class);

        public TestImageView(Context context) {
            super(context);
        }

        @Override
        public void setImageDrawable(Drawable drawable) {
            super.setImageDrawable(drawable);
            dummy.setImageDrawable(drawable);
        }

    }

    static class TestImageTransformer implements ImageRequestTransformer {

        @Override
        public ImageRequestBuilder transform(ImageRequestBuilder imageRequestBuilder) {
            return imageRequestBuilder.url(transformString(imageRequestBuilder.url));
        }

    }

    @NonNull
    static String transformString(String url) {
        return url.replace('t', 'a');
    }

}
