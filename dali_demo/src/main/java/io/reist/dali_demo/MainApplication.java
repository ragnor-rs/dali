package io.reist.dali_demo;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.SparseArray;

import java.util.Random;

import io.reist.dali.Dali;
import io.reist.dali.DaliImageFactory;

/**
 * Created by Reist on 10.06.16.
 */
public class MainApplication extends Application {

    private static final long SEED = 4843976917103476183L;

    private final static Random RANDOM = new Random(SEED);

    private final static SparseArray<Bitmap> DATA = new SparseArray<>();

    static {
        Dali.setImageFactory(new DaliImageFactory() {

            @Override
            public Bitmap load(String url) {
                int key = DaliUtils.toDaliKey(url);
                Bitmap bitmap = DATA.get(key);
                if (bitmap == null) {
                    bitmap = Bitmap.createBitmap(2, 2, Bitmap.Config.RGB_565);
                    DATA.put(key, bitmap);
                }
                return bitmap;
            }

        });
    }

}
