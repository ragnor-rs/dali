package io.reist.dali;

import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.bumptech.glide.load.resource.bitmap.StreamBitmapDecoder;

import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.internal.ShadowExtractor;

import java.io.InputStream;

/**
 * Created by Reist on 15.06.16.
 */
@Implements(StreamBitmapDecoder.class)
public class GlideShadowStreamBitmapDecoder {

    @SuppressWarnings("unused")
    @Implementation
    public Resource<Bitmap> decode(InputStream source, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565);
        String model = ((TestInputStream) source).getModel();
        Object shadow = ShadowExtractor.extract(bitmap);
        if (shadow instanceof TestShadowBitmap) {
            TestShadowBitmap shadowBitmap = (TestShadowBitmap) shadow;
            int actualKey = AsyncTestImageLoader.urlToKey(model);
            shadowBitmap.setActualKey(actualKey);
        }
        System.out.println("decode(" + model + ")");
        return BitmapResource.obtain(
                bitmap,
                Glide.get(RuntimeEnvironment.application).getBitmapPool()
        );
    }

    @SuppressWarnings("unused")
    public GlideShadowStreamBitmapDecoder() {}

}
