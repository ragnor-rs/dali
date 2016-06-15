package io.reist.dali.glide;

import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.bumptech.glide.load.resource.bitmap.StreamBitmapDecoder;

import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

import java.io.InputStream;

import io.reist.dali.TestUtils;

/**
 * Created by Reist on 15.06.16.
 */
@Implements(StreamBitmapDecoder.class)
public class GlideShadowStreamBitmapDecoder {

    @SuppressWarnings("unused")
    @Implementation
    public Resource<Bitmap> decode(InputStream source, int width, int height) {
        String model = ((TestInputStream) source).getModel();
        Bitmap bitmap = TestUtils.decode(TestUtils.urlToKey(model));
        return BitmapResource.obtain(
                bitmap,
                Glide.get(RuntimeEnvironment.application).getBitmapPool()
        );
    }

    @SuppressWarnings("unused")
    public GlideShadowStreamBitmapDecoder() {}

}
