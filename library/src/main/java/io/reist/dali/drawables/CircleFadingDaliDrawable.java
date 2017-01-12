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

package io.reist.dali.drawables;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reist.dali.ScaleMode;

public class CircleFadingDaliDrawable extends FadingDaliDrawable {

    public CircleFadingDaliDrawable(
            @Nullable Bitmap bitmap,
            @NonNull ScaleMode scaleMode,
            float targetWidth,
            float targetHeight,
            @Nullable Drawable placeholder,
            @Nullable Bitmap placeholderBitmap,
            boolean noFade
    ) {
        super(bitmap, scaleMode, targetWidth, targetHeight, placeholder, placeholderBitmap, noFade);
    }

    @Override
    protected void drawBitmap(@NonNull Canvas canvas, RectF dst, Paint paint) {
        float radius = Math.min(targetWidth, targetHeight) / 2;
        canvas.drawCircle(
                dst.centerX(),
                dst.centerY(),
                radius,
                paint
        );
    }

    @Override
    protected void drawPlaceholder(@NonNull Canvas canvas, RectF dst, Paint paint) {
        float radius = Math.min(targetWidth, targetHeight) / 2;
        canvas.drawCircle(
                dst.centerX(),
                dst.centerY(),
                radius,
                paint
        );
    }

}
