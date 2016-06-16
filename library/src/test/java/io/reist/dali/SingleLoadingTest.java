package io.reist.dali;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;

public abstract class SingleLoadingTest {

    public static final String TEST_URL = "0";

    private MainThread mainThread;

    private boolean onSetImageDrawableCalled;

    @Before
    public void setUp() {
        onSetImageDrawableCalled = false;
        mainThread = new MainThread();
    }

    @Test
    public void testLoadIntoImageView() {

        TestActivity activity = createActivity();

        ViewGroup rootView = (ViewGroup) activity.findViewById(android.R.id.content);
        TestImageView view = (TestImageView) rootView.getChildAt(0);

        Dali.load(TEST_URL).defer(false).into(view);

        // wait until the request is executed and the result is posted to the main thread
        waitForResult();

    }

    @NonNull
    protected TestActivity createActivity() {
        TestActivity activity = Robolectric.setupActivity(TestActivity.class);
        activity.setTest(this);
        return activity;
    }

    @Test
    public void testLoadWithDaliCallback() {

        Dali.load(TEST_URL).into(new DaliCallback() {

            @Override
            public void onImageLoaded(Bitmap bitmap) {
                notifyAboutResult();
            }

        }, RuntimeEnvironment.application);

        // wait until the request is executed and the result is posted to the main thread
        waitForResult();

    }

    protected static class TestActivity extends Activity implements TestImageView.Callback {

        private SingleLoadingTest test;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(new TestImageView(this, this));
        }

        @Override
        public void onSetImageDrawable(int expectedKey, int actualKey) {
            test.notifyAboutResult();
        }

        public void setTest(SingleLoadingTest test) {
            this.test = test;
        }

    }

    public void waitForResult() {
        if (!onSetImageDrawableCalled) {
            mainThread.loop();
            Assert.assertTrue("onSetImageDrawable wasn't called", onSetImageDrawableCalled);
        }
    }

    public void notifyAboutResult() {
        mainThread.stop();
        onSetImageDrawableCalled = true;
    }

}
