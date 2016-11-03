package io.reist.dali_demo;

import android.support.annotation.NonNull;
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
    public void load(@NonNull ImageRequestBuilder builder, @NonNull View view, boolean background) {
        ImageView imageView = (ImageView) view;
        ImageService.set(imageView, builder.url);
    }

    @Override
    public void load(@NonNull ImageRequestBuilder builder, @NonNull DaliCallback callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void cancel(@NonNull Object o) {
        ImageService.cancel((ImageView) o);
    }

    @Override
    public void cancelAll() {
        ImageService.cancelAll();
    }

}
