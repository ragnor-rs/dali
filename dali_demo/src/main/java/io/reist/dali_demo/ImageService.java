package io.reist.dali_demo;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageView;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Emulates slow image loading. Runs image load tasks concurrently.
 *
 * Created by Reist on 10.06.16.
 */
public class ImageService {

    private final static Random RANDOM = new Random();

    private final static SparseArray<BitmapDrawable> DATA = new SparseArray<>();

    private final static ExecutorService executor = Executors.newFixedThreadPool(4);

    private final static Map<ImageView, Task> taskMap = new ConcurrentHashMap<>();

    private static final String TAG = ImageService.class.getSimpleName();

    public static void set(final ImageView view, String url) {

        final int key = urlToPosition(url);

        BitmapDrawable drawable = DATA.get(key);

        if (drawable == null) {
            Task task = new Task(key, view);
            executor.submit(task);
            taskMap.put(view, task);
        } else {
            Log.d(TAG, "Cache hit: " + url);
            view.setImageDrawable(drawable);
        }

    }

    @NonNull
    private static BitmapDrawable createRandomBitmapDrawable() {
        BitmapDrawable drawable;
        Bitmap bitmap = Bitmap.createBitmap(2, 2, Bitmap.Config.RGB_565);
        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                bitmap.setPixel(i, j, RANDOM.nextInt());
            }
        }
        drawable = new BitmapDrawable(null, bitmap);
        return drawable;
    }

    public static void cancel(ImageView view) {
        Task task = taskMap.get(view);
        if (task != null) {
            task.cancel();
        }
    }

    public static int urlToPosition(String url) {
        return Integer.parseInt(url);
    }

    public static String positionToUrl(int position) {
        return Integer.toString(position);
    }

    public static void cancelAll() {
        for (Task task : taskMap.values()) {
            task.cancel();
        }
    }

    static class Task implements Runnable {

        private final int key;
        private final ImageView view;

        private volatile boolean cancelled;

        Task(int key, ImageView view) {
            this.key = key;
            this.view = view;
        }

        @Override
        public void run() {

            try {

                // look up a bitmap
                final BitmapDrawable drawable = decode(key);

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
                HANDLER.post(new Runnable() {

                    @Override
                    public void run() {

                        if (parent.cancelled) {
                            System.out.println("cancel done for " + parent.key);
                            return;
                        }

                        parent.view.setImageDrawable(drawable);

                    }

                });

            } finally {
                taskMap.remove(view);
            }

        }

        public void cancel() {
            cancelled = true;
        }

        @NonNull
        public static BitmapDrawable decode(int key) {
            BitmapDrawable drawable = DATA.get(key);
            if (drawable == null) {
                drawable = createRandomBitmapDrawable();
                DATA.put(key, drawable);
            }
            System.out.println("decode(" + key + ")");
            return drawable;
        }

    }

    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

}
