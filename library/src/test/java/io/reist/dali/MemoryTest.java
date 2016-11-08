package io.reist.dali;

import android.app.Activity;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.shadows.ShadowNetwork;
import org.robolectric.util.ActivityController;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.fail;

/**
 * Created by Reist on 08.11.16.
 */

@RunWith(RobolectricGradle3TestRunner.class)
@Config(
        constants = BuildConfig.class,
        sdk = Build.VERSION_CODES.JELLY_BEAN,
        shadows = {ShadowNetwork.class, ShadowNetworkSecurityPolicy.class}
)
public class MemoryTest {

    private static final long UI_TIMEOUT = TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES);

    private static final long LOAD_PERIOD =  TimeUnit.MILLISECONDS.convert(500, TimeUnit.MILLISECONDS);

    private static final String[] IMAGE_URLS = new String[] {
            "https://static.pexels.com/photos/6548/cold-snow-winter-mountain.jpeg",
            "http://os1.i.ua/1/11/560705.jpg",
            "https://newevolutiondesigns.com/images/freebies/city-wallpaper-47.jpg"
    };

    private static final Random RANDOM = new Random();

    private static final long MEM_DUMP_PERIOD = TimeUnit.MILLISECONDS.convert(5, TimeUnit.SECONDS);

    private static final int MEM_CHECKS_MIN = (IMAGE_URLS.length + 1) * 2;
    private static final int MEM_CHECKS_MAX = MEM_CHECKS_MIN * 2;

    @Test
    public void viewBounds() {

        final ActivityController<TestActivity> activityController = Robolectric.buildActivity(TestActivity.class);
        final TestActivity testActivity = activityController.create().start().resume().visible().get();

        testMem(new Runnable() {

            @Override
            public void run() {

                ImageView imageView = testActivity.fragment.imageView;

                Dali.with(testActivity)
                        .load(IMAGE_URLS[RANDOM.nextInt(IMAGE_URLS.length)])
                        .placeholder(R.drawable.placeholder)
                        .into(imageView);


//                final List<Object> list = new ArrayList<>();
//                list.add(new Object() {
//
//                    List<BigInteger> objects = new ArrayList<>();
//
//                    {
//                        for (int i = 0; i < 1000000; i++) {
//                            objects.add(new BigInteger(Integer.toString(RANDOM.nextInt(Integer.MAX_VALUE))));
//                        }
//                    }
//
//                });

            }

        });

        activityController.pause().stop().destroy();

    }

    protected static void testMem(Runnable r) {

        long startTime = System.currentTimeMillis();
        long lastLoadTime = startTime;
        long lastMemDumpTime = startTime;

        Runtime runtime = Runtime.getRuntime();

        long currentMinFreeMemory = -1;
        long currentMaxFreeMemory = -1;
        long memCheck = 0;
        long currentMaxTotalMemory = -1;
        long lastMaxTotalMemory = -1;

        while (true) {

            TestUtils.delay(40);

            // do UI work
            long currentTime = System.currentTimeMillis();
            if (currentTime - startTime > UI_TIMEOUT) {
                break;
            }
            ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

            // periodically do the work under test
            if (currentTime - lastLoadTime > LOAD_PERIOD) {
                r.run();
                lastLoadTime = currentTime;
            }

            // periodically check memory status
            if (currentTime - lastMemDumpTime > MEM_DUMP_PERIOD) {

                // measure min, current, max
                long currentFreeMemory = runtime.freeMemory();
                if (currentMinFreeMemory == -1 || currentFreeMemory < currentMinFreeMemory) {
                    currentMinFreeMemory = currentFreeMemory;
                }
                if (currentMaxFreeMemory == -1 || currentFreeMemory > currentMaxFreeMemory) {
                    currentMaxFreeMemory = currentFreeMemory;
                }
                long currentTotalMemory = runtime.totalMemory();
                if (currentMaxTotalMemory == -1 || currentTotalMemory > currentMaxTotalMemory) {
                    currentMaxTotalMemory = currentTotalMemory;
                }
                System.out.println("Free: " + currentFreeMemory);
                System.out.println("Min free: " + currentMinFreeMemory);
                System.out.println("Max free: " + currentMaxFreeMemory);
                System.out.println("Total: " + currentTotalMemory);
                System.out.println("Max total: " + currentMaxTotalMemory);
                System.out.println();

                if (lastMaxTotalMemory == -1) {
                    lastMaxTotalMemory = currentMaxTotalMemory;
                }

                // checks
                memCheck++;
                if (memCheck > MEM_CHECKS_MAX) {
                    break;
                } else if (memCheck > MEM_CHECKS_MIN) {
                    if (currentMaxTotalMemory > lastMaxTotalMemory) {
                        fail("Leak detected");
                    } else if (currentMaxTotalMemory < lastMaxTotalMemory) {
                        break; // GC freed memory = nothing is leaking
                    }
                }

                lastMemDumpTime = currentTime;

                lastMaxTotalMemory = currentMaxTotalMemory;

            }

            // keep memory usage to minimum
            System.gc();

        }

    }

    public static class TestActivity extends Activity {

        private static final int CONTAINER_ID = 1;

        private FrameLayout container;

        private TestFragment fragment;

        @SuppressWarnings("ResourceType")
        @Override
        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);

            container = new FrameLayout(this);
            container.setId(CONTAINER_ID);

            setContentView(container);

            fragment = new TestFragment();

            getFragmentManager()
                    .beginTransaction()
                    .add(CONTAINER_ID, fragment)
                    .commit();

        }

    }

    public static class TestFragment extends Fragment {

        private ImageView imageView;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return imageView = new ImageView(getActivity());
        }

    }

}
