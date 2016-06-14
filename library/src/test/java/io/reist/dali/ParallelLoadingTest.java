package io.reist.dali;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by Reist on 14.06.16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(
        constants = BuildConfig.class,
        sdk = {Build.VERSION_CODES.JELLY_BEAN}
)
public class ParallelLoadingTest {

    public static final int WINDOW_HEIGHT = 5;
    public static final int DATA_SET_LENGTH = 100;

    @BeforeClass
    public static void init() {
        Dali.setMainImageLoaderClass(TestImageLoader.class);
    }

    @Test
    public void perform() {

        TestActivity testActivity = Robolectric.setupActivity(TestActivity.class);

        for (int i = 0; i < 3; i++) {
            testActivity.recycler.setPosition(i * WINDOW_HEIGHT);
            testActivity.recycler.render();
        }

        try {
            Thread.sleep(15 * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Assert.assertEquals(
                "Out of sync",
                testActivity.getTotal(),
                testActivity.getSuccessful()
        );

    }

    static class TestImageView extends ImageView {

        private String key;
        private TestActivity activity;

        public TestImageView(TestActivity activity) {
            super(activity);
            this.activity = activity;
        }

        public TestImageView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public TestImageView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        public void setBindKey(String key) {
            this.key = key;
        }

        public void setImageDrawable(Drawable background, String requestKey) {
            super.setImageDrawable(background);
            activity.count(key.equals(requestKey));
            System.out.println("setImageDrawable @ " + key);
        }

    }

    static class TestActivity extends Activity {

        private volatile int total;
        private volatile int successful;

        // emulates main thread handler
        private final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(DATA_SET_LENGTH + WINDOW_HEIGHT);

        private Recycler<TestImageView> recycler;

        @Override
        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);

            recycler = new Recycler<>(WINDOW_HEIGHT, new Recycler.Adapter<TestImageView>() {

                @Override
                public int getCount() {
                    return DATA_SET_LENGTH;
                }

                @Override
                public TestImageView createView(int i) {
                    return new TestImageView(TestActivity.this);
                }

                @Override
                public void bindView(TestImageView testImageView, int i) {
                    String key = Integer.toString(i);
                    testImageView.setBindKey(key);
                    Dali.load(key)
                            .placeholder(android.R.color.black)
                            .targetSize(1, 1)
                            .into(testImageView);
                }

            });

            (new Thread() {

                @Override
                public void run() {
                    Runnable r;
                    try {
                        while ((r = queue.take()) != null) {
                            r.run();
                        }
                    } catch (InterruptedException ignored) {}
                }

            }).start();

        }

        public synchronized void count(boolean success) {
            total++;
            if (success) {
                successful++;
            }
        }

        public int getTotal() {
            return total;
        }

        public int getSuccessful() {
            return successful;
        }

    }

    static class TestImageLoader implements ImageLoader {

        private final static ExecutorService executor = Executors.newFixedThreadPool(4);

        private final static Map<TestImageView, Future<?>> taskMap = new ConcurrentHashMap<>();

        @Override
        public void load(ImageRequestBuilder builder, View view, boolean background) {
            TestImageView imageView = (TestImageView) view;
            taskMap.put(imageView, executor.submit(new Task(builder.url, imageView)));
        }

        @Override
        public void load(ImageRequestBuilder builder, DaliCallback callback, Context context) {
            throw new UnsupportedOperationException();
        }

        @SuppressWarnings("SuspiciousMethodCalls")
        @Override
        public void cancel(Object o) {
            System.out.println("Cancel request for " + ((TestImageView) o).key);
            Future<?> future = taskMap.get(o);
            if (future != null) {
                future.cancel(true);
            }
        }

        @Override
        public void cancelAll() {
            throw new UnsupportedOperationException();
        }

    }

    static class Task implements Runnable {

        private final String key;
        private final TestImageView view;

        Task(String key, TestImageView view) {
            this.key = key;
            this.view = view;
        }

        @Override
        public void run() {

            // emulate long loading
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ignored) {
                return;
            }

            // set the image
            final String finalKey = key;
            final TestImageView finalView = view;
            finalView.activity.queue.offer(new Runnable() {

                @Override
                public void run() {
                    System.out.println("Runnable for " + finalKey);
                    finalView.setImageDrawable(null, finalKey);
                }

            });

        }

    }

    static class Recycler<V extends View> {

        private final int windowHeight;
        private final Adapter<V> adapter;

        private int position;

        private final List<ViewHolder<V>> viewHolders = new ArrayList<>();

        Recycler(int windowHeight, Adapter<V> adapter) {
            this.windowHeight = windowHeight;
            this.adapter = adapter;
        }

        void render() {

            int dataLength = adapter.getCount();

            int windowStart = this.position;
            int windowEnd = Math.min(this.position + windowHeight, dataLength);

            // recycle invisible views
            for (int i = 0; i < windowStart; i++) {
                for (ViewHolder<V> holder : viewHolders) {
                    if (holder.i == i) {
                        holder.i = -1;
                    }
                }
            }

            // bind data to visible views
            for (int i = windowStart; i < windowEnd; i++) {
                ViewHolder<V> viewHolder = createOrGet(i);
                adapter.bindView(viewHolder.v, i);
            }

            // recycle invisible views
            for (int i = windowEnd; i < dataLength; i++) {
                for (ViewHolder<V> holder : viewHolders) {
                    if (holder.i == i) {
                        holder.i = -1;
                    }
                }
            }

        }

        ViewHolder<V> createOrGet(int i) {
            for (ViewHolder<V> holder : viewHolders) {
                if (holder.i == -1) {
                    return holder;
                }
            }
            ViewHolder<V> holder = new ViewHolder<>(adapter.createView(i), i);
            viewHolders.add(holder);
            return holder;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        static class ViewHolder<V> {
            private final V v;
            private int i;

            public ViewHolder(V v, int i) {
                this.v = v;
                this.i = i;
            }
        }

        interface Adapter<V extends View> {
            int getCount();

            V createView(int i);

            void bindView(V v, int i);
        }

    }

}
