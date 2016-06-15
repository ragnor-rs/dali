package io.reist.dali.main;

import android.content.Context;
import android.view.View;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import io.reist.dali.DaliCallback;
import io.reist.dali.ImageLoader;
import io.reist.dali.ImageRequestBuilder;
import io.reist.dali.TestImageView;
import io.reist.dali.TestUtils;

/**
 * Created by Reist on 15.06.16.
 */
public class AsyncTestImageLoader implements ImageLoader {

    private final static ExecutorService executor = Executors.newFixedThreadPool(4);

    private final static Map<TestImageView, Future<?>> taskMap = new ConcurrentHashMap<>();

    @Override
    public void load(ImageRequestBuilder builder, View view, boolean background) {
        TestImageView imageView = (TestImageView) view;
        int key = TestUtils.urlToKey(builder.url);
        taskMap.put(imageView, executor.submit(new Task(key, imageView)));
        System.out.println("Requested " + key);
    }

    @Override
    public void load(ImageRequestBuilder builder, DaliCallback callback, Context context) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    public void cancel(Object o) {
        Future<?> future = taskMap.get(o);
        if (future != null) {
            future.cancel(true);
            taskMap.remove(o);
        }
    }

    @Override
    public void cancelAll() {
        throw new UnsupportedOperationException();
    }

    /**
     * Just a prolonged image loading task. Doesn't perform actual loading.
     */
    static class Task implements Runnable {

        private final int key;
        private final TestImageView view;

        Task(int key, TestImageView view) {
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
            final int finalKey = key;
            final TestImageView finalView = view;
            RunnableQueue.getInstance().post(new Runnable() {

                @Override
                public void run() {
                    finalView.setActualKey(finalKey);
                    finalView.setImageDrawable(null);
                }

            });

        }

    }

}
