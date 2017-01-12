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
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import io.reist.dali.drawables.CircleFadingDaliDrawable;
import io.reist.dali.drawables.DaliDrawable;
import io.reist.dali.drawables.FadingDaliDrawable;

import static io.reist.dali.TestUtils.assertDrawable;
import static io.reist.dali.TestUtils.checkFadingDrawable;

/**
 * Created by Reist on 01.11.16.
 */

@RunWith(RobolectricTestRunner.class)
@Config(
        constants = BuildConfig.class,
        sdk = {Build.VERSION_CODES.JELLY_BEAN},
        shadows = ShadowViewTreeObserver.class
)
public class DrawableTest {

    @Test
    public void daliDrawable() {

        DaliDrawable daliDrawable = new DaliDrawable(
                Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888),
                ScaleMode.CENTER_INSIDE,
                1,
                1
        );
        assertDrawable(daliDrawable);

        // TODO check correctness of drawable size

    }

    @Test
    public void fadingDaliDrawable() throws InterruptedException {

        FadingDaliDrawable daliDrawable = new FadingDaliDrawable(
                Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888),
                ScaleMode.CENTER_INSIDE,
                1,
                1,
                new BitmapDrawable(
                        RuntimeEnvironment.application.getResources(),
                        Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                ),
                null,
                false
        );
        checkFadingDrawable(daliDrawable);

        // TODO check correctness of drawable size

    }

    @Test
    public void circleFadingDaliDrawable() throws InterruptedException {

        CircleFadingDaliDrawable daliDrawable = new CircleFadingDaliDrawable(
                Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888),
                ScaleMode.CENTER_INSIDE,
                1,
                1,
                new BitmapDrawable(
                        RuntimeEnvironment.application.getResources(),
                        Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                ),
                null,
                false
        );
        checkFadingDrawable(daliDrawable);

        // TODO check correctness of drawable size

    }

}


