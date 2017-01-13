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

package io.reist.dali.main;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import org.mockito.Mockito;

import io.reist.dali.DaliCallback;
import io.reist.dali.ImageLoader;
import io.reist.dali.ImageRequest;

/**
 * Created by Reist on 15.06.16.
 */
public class SyncTestImageLoader implements ImageLoader {

    private String url;

    @Override
    public void load(@NonNull ImageRequest request, @NonNull View view, boolean background) {
        this.url = request.url;
        if (view instanceof ImageView) {
            ((ImageView) view).setImageDrawable(Mockito.mock(Drawable.class));
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void load(@NonNull ImageRequest request, @NonNull DaliCallback callback) {
        this.url = request.url;
        callback.onImageLoaded(Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565));
    }

    @Override
    public void cancel(@NonNull Object target) {}

    @Override
    public void cancelAll() {}

    String getUrl() {
        return url;
    }

}
