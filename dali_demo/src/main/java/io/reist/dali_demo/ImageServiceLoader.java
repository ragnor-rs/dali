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

package io.reist.dali_demo;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import io.reist.dali.DaliCallback;
import io.reist.dali.ImageLoader;
import io.reist.dali.ImageRequest;

/**
 * Uses {@link ImageService} as a image provider
 *
 * Created by Reist on 10.06.16.
 */
public class ImageServiceLoader implements ImageLoader {

    @Override
    public void load(@NonNull ImageRequest request, @NonNull View view, boolean background) {
        ImageView imageView = (ImageView) view;
        ImageService.set(imageView, request.url);
    }

    @Override
    public void load(@NonNull ImageRequest request, @NonNull DaliCallback callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void cancel(@NonNull Object target) {
        ImageService.cancel((ImageView) target);
    }

    @Override
    public void cancelAll() {
        ImageService.cancelAll();
    }

}
