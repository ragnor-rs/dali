package io.reist.dali;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import org.mockito.Mockito;
import org.robolectric.internal.ShadowExtractor;

import io.reist.dali.drawables.FadingDaliDrawable;

/**
 * Created by Reist on 15.06.16.
 */
public class TestImageView extends ImageView {

    private final Callback callback;

    private int expectedKey = -1;

    public TestImageView(Context context) {
        this(context, null);
    }

    public TestImageView(Context context, Callback callback) {
        super(context);
        this.callback = callback;
    }

    @Override
    public void setImageDrawable(Drawable drawable) {

        super.setImageDrawable(drawable);

        int actualKey = -1;

        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Object shadow = ShadowExtractor.extract(bitmap);
            if (shadow instanceof TestShadowBitmap) {
                TestShadowBitmap shadowBitmap = (TestShadowBitmap) shadow;
                actualKey = shadowBitmap.getActualKey();
            }
        } else if (drawable instanceof FadingDaliDrawable) {
            Object shadow = ShadowExtractor.extract(drawable);
            if (shadow instanceof ShadowFadingDaliDrawable) {
                ShadowFadingDaliDrawable shadowDrawable = (ShadowFadingDaliDrawable) shadow;
                actualKey = shadowDrawable.getKey();
            }
        }

        if (callback != null) {
            callback.onSetImageDrawable(expectedKey, actualKey);
        }

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
        assertSetDrawableCalled();
    }

    public void assertSetDrawableCalled() {
        Mockito.verify(dummy).setImageDrawable(Mockito.any(Drawable.class));
    }

    public void setExpectedKey(int expectedKey) {
        this.expectedKey = expectedKey;
    }

    public int getExpectedKey() {
        return expectedKey;
    }

    public interface Callback {
        void onSetImageDrawable(int expectedKey, int actualKey);
    }

    private final ImageView dummy = Mockito.mock(ImageView.class);

}
