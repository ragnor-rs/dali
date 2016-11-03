package io.reist.dali.main;

import android.support.annotation.NonNull;

import io.reist.dali.ImageRequest;
import io.reist.dali.ImageRequestTransformer;

/**
 * Created by Reist on 15.06.16.
 */
class TestImageTransformer implements ImageRequestTransformer {

    @NonNull
    static String transformString(String url) {
        return url.replace('t', 'a');
    }

    @Override
    public ImageRequest transform(@NonNull ImageRequest imageRequest) {
        imageRequest.url = transformString(imageRequest.url);
        return imageRequest;
    }

}
