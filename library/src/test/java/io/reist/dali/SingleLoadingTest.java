/*
 * Copyright (C) 2017 Renat Sarymsakov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

        Dali.with(activity).load(TEST_URL).defer(false).into(view);

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

        Dali.with(RuntimeEnvironment.application).load(TEST_URL).into(new DaliCallback() {

            @Override
            public void onImageLoaded(@NonNull Bitmap bitmap) {
                notifyAboutResult();
            }

        });

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
