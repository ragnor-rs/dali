package io.reist.dali;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.ViewGroup;

import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowLooper;

public abstract class SingleLoadingTest {

    public static final String TEST_URL = "test";

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

    protected static class TestActivity extends Activity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(new TestImageView(this));
        }

    }

}
