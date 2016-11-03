package io.reist.dali.main;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import org.mockito.Mockito;

import io.reist.dali.DaliCallback;
import io.reist.dali.ImageLoader;
import io.reist.dali.ImageRequestBuilder;

/**
 * Created by Reist on 15.06.16.
 */
public class SyncTestImageLoader implements ImageLoader {

    private String url;

    @Override
    public void load(@NonNull ImageRequestBuilder builder, @NonNull View view, boolean background) {
        this.url = builder.url;
        if (view instanceof ImageView) {
            ((ImageView) view).setImageDrawable(Mockito.mock(Drawable.class));
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void load(@NonNull ImageRequestBuilder builder, @NonNull DaliCallback callback) {
        this.url = builder.url;
        callback.onImageLoaded(Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565));
    }

    @Override
    public void cancel(@NonNull Object o) {}

    @Override
    public void cancelAll() {}

    String getUrl() {
        return url;
    }

}
