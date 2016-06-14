package io.reist.dali;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import org.mockito.Mockito;
import org.robolectric.internal.ShadowExtractor;

/**
 * Created by Reist on 15.06.16.
 */
class TestImageView extends ImageView {

    private final Callback callback;

    private int key;

    public TestImageView(Context context) {
        this(context, null);
    }

    public TestImageView(Context context, Callback callback) {
        super(context);
        this.callback = callback;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public void setImageDrawable(Drawable drawable, int requestKey) {
        System.out.println(this + ".setImageDrawable @ " + key + ", " + requestKey);
        setImageDrawable(drawable);
        if (callback != null) {
            callback.onSetImageDrawable(key, requestKey);
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        dummy.setImageDrawable(drawable);
    }

    public void assertMeasureDoesNotSetDrawable() {
        measure(
                View.MeasureSpec.makeMeasureSpec(100, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(100, View.MeasureSpec.EXACTLY)
        );
        Mockito.verify(dummy, Mockito.times(0)).setImageDrawable(Mockito.any(Drawable.class));
    }

    public void assertLayoutSetsDrawable() {
        layout(0, 0, 100, 100);
        ShadowViewTreeObserver viewTreeObserver =
                (ShadowViewTreeObserver) ShadowExtractor.extract(getViewTreeObserver());
        viewTreeObserver.fireOnPreDrawListeners();
        Mockito.verify(dummy, Mockito.times(1)).setImageDrawable(Mockito.any(Drawable.class));
    }

    interface Callback {
        void onSetImageDrawable(int actualKey, int expectedKey);
    }

    private final ImageView dummy = Mockito.mock(ImageView.class);

}
