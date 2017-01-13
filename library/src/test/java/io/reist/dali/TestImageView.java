/*
 * Copyright (C) 2017 Renat Sarymsakov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.reist.dali;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import org.mockito.Mockito;
import org.robolectric.internal.ShadowExtractor;

import io.reist.dali.drawables.DaliDrawable;
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

        if (drawable instanceof DaliDrawable) {
            if (!((DaliDrawable) drawable).hasBitmap()) { // ignore placeholders
                return;
            }
        }

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
