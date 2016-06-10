package io.reist.dali_demo;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.util.SparseArray;
import android.widget.ImageView;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Emulates slow image loading. Runs image load tasks concurrently.
 *
 * Created by Reist on 10.06.16.
 */
public class ImageService {

    private final static Random RANDOM = new Random();

    private final static SparseArray<BitmapDrawable> DATA = new SparseArray<>();

    private final static ExecutorService executor = Executors.newFixedThreadPool(5);

    private final static Map<ImageView, Future<?>> taskMap = new ConcurrentHashMap<>();

    public static void set(final ImageView view, int key) {

        BitmapDrawable drawable = DATA.get(key);
        if (drawable == null) {
            drawable = createRandomBitmapDrawable();
            DATA.put(key, drawable);
        }

        final BitmapDrawable finalDrawable = drawable;
        taskMap.put(view, executor.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    view.setImageDrawable(finalDrawable);
                } catch (InterruptedException ignored) {}
            }

        }));

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

}
