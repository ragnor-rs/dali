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
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.shadows.ShadowNetwork;
import org.robolectric.util.ActivityController;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.fail;

/**
 * Created by Reist on 08.11.16.
 */

@RunWith(RobolectricTestRunner.class)
@Config(
        constants = BuildConfig.class,
        sdk = Build.VERSION_CODES.JELLY_BEAN,
        shadows = {ShadowNetwork.class, ShadowNetworkSecurityPolicy.class}
)
public class MemoryTest {

    private static final long SCREEN_LIFETIME =  TimeUnit.MILLISECONDS.convert(20, TimeUnit.SECONDS);
    private static final long LOAD_PERIOD =  TimeUnit.MILLISECONDS.convert(600, TimeUnit.MILLISECONDS);

    private static final long MEM_DUMP_PERIOD = TimeUnit.MILLISECONDS.convert(5, TimeUnit.SECONDS);

    //

    private static final String[] IMAGE_URLS = new String[] {
            "https://static.pexels.com/photos/6548/cold-snow-winter-mountain.jpeg",
            "http://os1.i.ua/1/11/560705.jpg",
            "https://newevolutiondesigns.com/images/freebies/city-wallpaper-47.jpg",
            "http://test.schoolchildparents.com/assets/images/flower1.jpg",
            "http://www.rabstol.net/uploads/gallery/main/553/rabstol_net_fire_04.jpg"
    };

    private static final Random RANDOM = new Random();

    private static final int MEM_CHECKS_MIN = IMAGE_URLS.length + 1;
    private static final int MEM_CHECKS_MAX = MEM_CHECKS_MIN * 2;

    private volatile boolean finish = false;
    private volatile boolean leak = false;

    @Test
    public void fragmentSwitching() {

        new MemoryChecker().start();

        doUiWork();

        if (leak) {
            fail("Leak detected");
        }

    }

    public static class TestActivity extends Activity {

        private static final int CONTAINER_ID = 1;

        private FrameLayout container;

        TestFragment fragment;

        @SuppressWarnings("ResourceType")
        @Override
        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);

            container = new FrameLayout(this);
            container.setId(CONTAINER_ID);

            setContentView(container);

       }

        @SuppressWarnings("ResourceType")
        protected void switchFragments() {

            FragmentTransaction fragmentTransaction = getFragmentManager()
                    .beginTransaction();

            if (fragment != null) {
                fragmentTransaction
                        .remove(fragment);
            }

            fragment = new TestFragment();

            fragmentTransaction = fragmentTransaction
                    .add(CONTAINER_ID, fragment);

            fragmentTransaction
                    .commit();

        }

    }

    public static class TestFragment extends Fragment {

        ImageView imageView;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return imageView = new ImageView(getActivity());
        }

        protected void loadImage() {
            Dali.with(imageView)
                    .load(IMAGE_URLS[RANDOM.nextInt(IMAGE_URLS.length)])
                    .placeholder(R.drawable.placeholder)
                    .into(imageView);
        }

    }

    private TestActivity testActivity;
    private ActivityController<TestActivity> activityController;

    public void doUiWork() {

        long lastRecreateTime = System.currentTimeMillis();
        long lastLoadTime = System.currentTimeMillis();

        createActivity();

        switchFragments();
        loadImage();

        while (!finish && !leak) {

            TestUtils.delay(40);
            ShadowLooper.runMainLooperToNextTask();

            long currentTime = System.currentTimeMillis();

            if (currentTime - lastRecreateTime > SCREEN_LIFETIME) {
                switchFragments();
                lastRecreateTime = currentTime;
            }

            if (currentTime - lastLoadTime > LOAD_PERIOD) {
                loadImage();
                lastLoadTime = currentTime;
            }

        }

        destroyActivity();

    }


    //List<Object> list = new ArrayList<>();

    protected void loadImage() {

        testActivity.fragment.loadImage();

//        list.add(new Object() {
//
//            List<BigInteger> objects = new ArrayList<>();
//
//            {
//                for (int i = 0; i < 1000000; i++) {
//                    objects.add(new BigInteger(Integer.toString(RANDOM.nextInt(Integer.MAX_VALUE))));
//                }
//            }
//
//        });

    }

    protected void switchFragments() {
        testActivity.switchFragments();
    }

    private void destroyActivity() {
        activityController.pause().stop().destroy();
        testActivity = null;
        activityController = null;
    }

    private void createActivity() {
        activityController = Robolectric.buildActivity(TestActivity.class);
        testActivity = activityController.create().start().resume().visible().get();
    }

    private class MemoryChecker extends Thread {

        @Override
        public void run() {

            Runtime runtime = Runtime.getRuntime();

            long memCheck = 0;

            long maxUsedMemory = -1;
            long maxTotalMemory = -1;

            long lastMaxUsedMemory = -1;
            long lastMaxTotalMemory = -1;

            while (!finish && !leak) {

                TestUtils.delay(MEM_DUMP_PERIOD);

                // measure min, current, max
                long totalMemory = runtime.totalMemory();
                if (maxTotalMemory == -1 || totalMemory > maxTotalMemory) {
                    maxTotalMemory = totalMemory;
                }
                long usedMemory = totalMemory - runtime.freeMemory();
                if (maxUsedMemory == -1 || usedMemory > maxUsedMemory) {
                    maxUsedMemory = usedMemory;
                }

                // init if needed
                if (lastMaxTotalMemory == -1) {
                    lastMaxTotalMemory = maxTotalMemory;
                }
                if (lastMaxUsedMemory == -1) {
                    lastMaxUsedMemory = maxUsedMemory;
                }

                // checks
                memCheck++;
                if (memCheck >= MEM_CHECKS_MAX) {
                    finish = true;
                } else if (memCheck >= MEM_CHECKS_MIN) {
                    if (maxUsedMemory > lastMaxUsedMemory && maxTotalMemory > lastMaxTotalMemory) {
                        leak = true;
                    }
                }

                // print stats
                System.out.println("Count: " + memCheck + "/" + MEM_CHECKS_MAX);
                System.out.println("Max total: " + maxTotalMemory);
                System.out.println("Max used: " + maxUsedMemory);
                System.out.println();

                // keep memory usage to minimum
                System.gc();

                lastMaxTotalMemory = maxTotalMemory;
                lastMaxUsedMemory = maxUsedMemory;

            }

        }

    }

}
