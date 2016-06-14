package io.reist.dali_demo;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import io.reist.dali.DaliCallback;
import io.reist.dali.ImageLoader;
import io.reist.dali.ImageRequestBuilder;

/**
 * Uses {@link ImageService} as a image provider
 *
 * Created by Reist on 10.06.16.
 */
public class ImageServiceLoader implements ImageLoader {

    @Override
    public void load(ImageRequestBuilder builder, View view, boolean background) {
        ImageView imageView = (ImageView) view;
        ImageService.set(imageView, builder.url);
    }

    @Override
    public void load(ImageRequestBuilder builder, DaliCallback callback, Context context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void cancel(Object o) {
        ImageService.cancel((ImageView) o);
    }

    @Override
    public void cancelAll() {
        ImageService.cancelAll();
    }

}
