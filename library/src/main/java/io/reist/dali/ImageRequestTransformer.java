package io.reist.dali;

import android.support.annotation.NonNull;

/**
 * Implement this to transform incoming requests.
 */
public interface ImageRequestTransformer {

    ImageRequestBuilder transform(@NonNull ImageRequestBuilder imageRequestBuilder);

    ImageRequestTransformer IDENTITY = new ImageRequestTransformer() {

        @Override
        public ImageRequestBuilder transform(@NonNull ImageRequestBuilder imageRequestBuilder) {
            return imageRequestBuilder;
        }

    };

}
