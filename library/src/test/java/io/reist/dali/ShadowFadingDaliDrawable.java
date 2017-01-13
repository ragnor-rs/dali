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

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.robolectric.annotation.Implements;
import org.robolectric.internal.ShadowExtractor;
import org.robolectric.shadows.ShadowDrawable;

import io.reist.dali.drawables.FadingDaliDrawable;

/**
 * Created by Reist on 01.11.16.
 */

@Implements(FadingDaliDrawable.class)
public class ShadowFadingDaliDrawable extends ShadowDrawable {

    private int key = -1;

    public void __constructor__(
            @Nullable Bitmap bitmap,
            @NonNull ScaleMode scaleMode,
            float targetWidth,
            float targetHeight,
            @Nullable Drawable placeholder,
            @Nullable Bitmap placeholderBitmap,
            boolean noFade
    ) {
        Object shadow = null;
        if (bitmap != null) {
            shadow = ShadowExtractor.extract(bitmap);
        }
        if (shadow instanceof TestShadowBitmap) {
            TestShadowBitmap shadowBitmap = (TestShadowBitmap) shadow;
            key = shadowBitmap.getActualKey();
        }
    }

    public int getKey() {
        return key;
    }

}
