package io.reist.dali_demo;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageView;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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

    private final static Map<ImageView, Future<?>> taskMap = new ConcurrentHashMap<>();

    private static final String TAG = ImageService.class.getSimpleName();

    public static void set(final ImageView view, String url) {

        final int key = urlToPosition(url);

        BitmapDrawable drawable = DATA.get(key);

        if (drawable == null) {
            taskMap.put(view, executor.submit(new Task(key, view)));
        } else {
            Log.i(TAG, "Cache hit: " + url);
            view.setImageDrawable(drawable);
        }

    }

    @NonNull
    private static BitmapDrawable createRandomBitmapDrawable() {
        BitmapDrawable drawable;Bitmap bitmap = Bitmap.createBitmap(2, 2, Bitmap.Config.RGB_565);
        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                bitmap.setPixel(i, j, RANDOM.nextInt());
            }
        }
        drawable = new BitmapDrawable(null, bitmap);
        return drawable;
    }

    public static void cancel(ImageView view) {
        Future<?> future = taskMap.get(view);
        if (future != null) {
            future.cancel(true);
        }
    }

    public static int urlToPosition(String url) {
        return Integer.parseInt(url);
    }

    public static String positionToUrl(int position) {
        return Integer.toString(position);
    }

    static class Task implements Runnable {

        private final int key;
        private final ImageView view;

        Task(int key, ImageView view) {
            this.key = key;
            this.view = view;
        }

        @Override
        public void run() {

            Log.d(TAG, "Requested a bitmap for key: " + key);

            // look up a bitmap
            BitmapDrawable drawable = DATA.get(key);
            if (drawable == null) {
                drawable = createRandomBitmapDrawable();
                DATA.put(key, drawable);
            }

            // emulate long loading
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ignored) {
                Log.d(TAG, "Interrupted loading for key: " + key);
            }

            // set the image
            final BitmapDrawable finalDrawable = drawable;
            view.post(new Runnable() {

                @Override
                public void run() {
                    view.setImageDrawable(finalDrawable);
                }

            });

            Log.d(TAG, "Loaded key: " + key);

        }

    }

}
