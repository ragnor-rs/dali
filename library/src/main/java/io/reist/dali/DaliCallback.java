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
import android.support.annotation.NonNull;

/**
 * An object which uses images loaded by {@link Dali}. Basically, it's a view target, same as
 * {@link android.view.View}, but it doesn't have a background to load an image into.
 */
public interface DaliCallback {

    /**
     * Called when the bitmap is fully loaded. Please note that the bitmap may get recycled
     * after this method because of a particular {@link ImageLoader} implementation.
     * It is recommended to make a copy.
     */
    void onImageLoaded(@NonNull Bitmap bitmap);

}
