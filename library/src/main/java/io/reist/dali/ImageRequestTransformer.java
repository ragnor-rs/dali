package io.reist.dali;

/**
 * Implement this to transform incoming requests.
 */
public interface ImageRequestTransformer {

    ImageRequestBuilder transform(ImageRequestBuilder imageRequestBuilder);

    ImageRequestTransformer IDENTITY = new ImageRequestTransformer() {

        @Override
        public ImageRequestBuilder transform(ImageRequestBuilder imageRequestBuilder) {
            return imageRequestBuilder;
        }

    };

}
