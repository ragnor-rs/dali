package io.reist.dali;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import org.mockito.Mockito;

/**
 * Created by Reist on 15.06.16.
 */
class SyncTestImageLoader implements ImageLoader {

    private String url;

    @Override
    public void load(ImageRequestBuilder builder, View view, boolean background) {
        this.url = builder.url;
        if (view instanceof ImageView) {
            ((ImageView) view).setImageDrawable(Mockito.mock(Drawable.class));
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void load(ImageRequestBuilder builder, DaliCallback callback, Context context) {
        this.url = builder.url;
        callback.onImageLoaded(Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565));
    }

    @Override
    public void cancel(Object o) {}

    @Override
    public void cancelAll() {}

    public String getUrl() {
        return url;
    }

}
