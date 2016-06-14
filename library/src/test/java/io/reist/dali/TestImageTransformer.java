package io.reist.dali;

import android.support.annotation.NonNull;

/**
 * Created by Reist on 15.06.16.
 */
class TestImageTransformer implements ImageRequestTransformer {

    @NonNull
    static String transformString(String url) {
        return url.replace('t', 'a');
    }

    @Override
    public ImageRequestBuilder transform(ImageRequestBuilder imageRequestBuilder) {
        return imageRequestBuilder.url(transformString(imageRequestBuilder.url));
    }

}
