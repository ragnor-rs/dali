package io.reist.dali;

import android.support.annotation.NonNull;

/**
 * Implement this to transform incoming requests.
 */
public interface ImageRequestTransformer {

    ImageRequest transform(@NonNull ImageRequest imageRequest);

    ImageRequestTransformer IDENTITY = new ImageRequestTransformer() {

        @Override
        public ImageRequest transform(@NonNull ImageRequest imageRequest) {
            return imageRequest;
        }

    };

}
