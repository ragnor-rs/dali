package io.reist.dali;

/**
 * Created by m039 on 12/30/15.
 */
public interface ImageTransformer {

    ImageRequestBuilder transform(ImageRequestBuilder imageRequestBuilder);

    ImageTransformer IDENTITY = new ImageTransformer() {

        @Override
        public ImageRequestBuilder transform(ImageRequestBuilder imageRequestBuilder) {
            return imageRequestBuilder;
        }

    };

}
