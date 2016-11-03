package io.reist.dali_demo;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import io.reist.dali.DaliCallback;
import io.reist.dali.ImageLoader;
import io.reist.dali.ImageRequest;

/**
 * Uses {@link ImageService} as a image provider
 *
 * Created by Reist on 10.06.16.
 */
public class ImageServiceLoader implements ImageLoader {

    @Override
    public void load(@NonNull ImageRequest request, @NonNull View view, boolean background) {
        ImageView imageView = (ImageView) view;
        ImageService.set(imageView, request.url);
    }

    @Override
    public void load(@NonNull ImageRequest request, @NonNull DaliCallback callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void cancel(@NonNull Object target) {
        ImageService.cancel((ImageView) target);
    }

    @Override
    public void cancelAll() {
        ImageService.cancelAll();
    }

}
