package io.reist.dali;

/**
 * Created by Reist on 15.06.16.
 */
public class TestGlideImageLoader extends GlideImageLoader {

    @Override
    public void cancel(Object o) {
        super.cancel(o);
        System.out.println("cancel(" + ((TestImageView) o).getExpectedKey() + ")");
    }

}
