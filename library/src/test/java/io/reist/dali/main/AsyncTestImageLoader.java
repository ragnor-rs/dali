package io.reist.dali.main;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.view.View;

import org.robolectric.shadows.ShadowLooper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reist.dali.DaliCallback;
import io.reist.dali.ImageLoader;
import io.reist.dali.ImageRequest;
import io.reist.dali.TestImageView;
import io.reist.dali.TestUtils;

/**
 * Created by Reist on 15.06.16.
 */
public class AsyncTestImageLoader implements ImageLoader {

    private final static ExecutorService executor = Executors.newFixedThreadPool(4);

    private final static Map<TestImageView, Task> taskMap = new ConcurrentHashMap<>();

    @Override
    public void load(@NonNull ImageRequest request, @NonNull View view, boolean background) {
        TestImageView imageView = (TestImageView) view;
        int key = TestUtils.urlToKey(request.url);
        Task task = new Task(key, imageView);
        executor.submit(task);
        taskMap.put(imageView, task);
    }

    @Override
    public void load(@NonNull ImageRequest request, @NonNull DaliCallback callback) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    public void cancel(@NonNull Object target) {
        Task task = taskMap.get(target);
        if (task != null) {
            task.cancel();
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

        private volatile boolean cancelled;

        Task(int key, TestImageView view) {
            this.key = key;
            this.view = view;
        }

        @Override
        public void run() {

            try {

                // look up a bitmap
                final Bitmap bitmap = TestUtils.decode(key);

                // emulate long loading
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ignored) {}

                if (cancelled) {
                    System.out.println("cancel done for " + key);
                    return;
                }

                // set the image
                final Task parent = this;
                ShadowLooper.getShadowMainLooper().getScheduler().post(new Runnable() {

                    @Override
                    public void run() {

                        if (parent.cancelled) {
                            System.out.println("cancel done for " + parent.key);
                            return;
                        }

                        parent.view.setImageDrawable(new BitmapDrawable(
                                null,
                                bitmap
                        ));

                    }

                });

            } finally {
                taskMap.remove(view);
            }

        }

        public void cancel() {
            cancelled = true;
            System.out.println("cancel req for " + key);
        }

    }

}
