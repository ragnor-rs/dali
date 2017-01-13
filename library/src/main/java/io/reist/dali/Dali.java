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

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import io.reist.dali.glide.GlideImageLoader;

/**
 * Dali is an abstraction above asynchronous image loading libraries. The default implementation
 * uses {@link GlideImageLoader} which fetches images from the network via Glide library
 * (https://github.com/bumptech/glide). The underlying implementation can be changed by calling
 * {@link #setMainImageLoaderClass(Class)} before {@link ImageRequest#into(View)},
 * {@link ImageRequest#into(View, boolean)},
 * {@link ImageRequest#into(DaliCallback)} or {@link #cancel(Object)} are
 * called.
 *
 * Created by m039 on 12/30/15.
 */
public class Dali {

    private final Object attachTarget;
    private static final String TAG = Dali.class.getName();

    @SuppressWarnings("unused")
    public Context getApplicationContext() {
        return applicationContext;
    }

    private final Context applicationContext;

    private Dali(Object attachTarget) {
        this.attachTarget = attachTarget;
        this.applicationContext = DaliUtils.getApplicationContext(attachTarget);
    }

    private Dali() {
        this.attachTarget = null;
        this.applicationContext = null;
    }

    public ImageRequest load(String url) {
        if (attachTarget == null || applicationContext == null) {
            return new ImageRequest();
        } else {
            logUrl(url);
            return new ImageRequest(attachTarget).url(url);
        }
    }

    private void logUrl(String url) {
        if (DaliLoader.getInstance().isDebuggable()) {
            Log.d(TAG, url != null ? url: "null");
        }
    }

    public static Dali with(@NonNull Context context) {
        return new Dali(context);
    }

    public static Dali with(@NonNull android.app.Fragment fragment) {
        return new Dali(fragment);
    }

    public static Dali with(@NonNull android.support.v4.app.Fragment fragment) {
        return new Dali(fragment);
    }

    public static Dali with(@NonNull View view) {
        Activity activity = extractActivity(view.getContext());
        if (activity == null) {
            return stub();
        } else {
            return with(activity);
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static Dali stub() {
        return new Dali();
    }

    private static Activity extractActivity(Context context) {
        if (context instanceof Activity) {
            return ((Activity) context);
        } else if (context instanceof ContextWrapper) {
            Context parent = ((ContextWrapper) context).getBaseContext();
            return extractActivity(parent);
        } else {
            return null;
        }
    }

    /**
     * @see ImageLoader#cancel(Object)
     */
    @SuppressWarnings("unused")
    public static void cancel(@NonNull Object target) {
        DaliLoader.getInstance().cancel(target);
    }

    public static void setDebuggable(boolean debuggable) {
        DaliLoader.getInstance().setDebuggable(debuggable);
    }

    /**
     * Changes Dali main loader implementation. This loader will be used for {@link View}s of known
     * dimensions and {@link DaliCallback}.
     *
     * @see GlideImageLoader     default main image loader
     *
     */
    @SuppressWarnings("unused")
    public static void setMainImageLoaderClass(Class<? extends ImageLoader> imageLoaderClass) {
        DaliLoader.getInstance().initMainImageLoader(imageLoaderClass);
    }

    /**
     * Changes Dali deferred image loader implementation. This loader will be used for {@link View}s
     * which have not been measured yet
     *
     * @see DeferredImageLoader     default deferred image loader
     */
    @SuppressWarnings("unused")
    public static void setDeferredImageLoaderClass(Class<? extends DeferredImageLoader> imageLoaderClass) {
        DaliLoader.getInstance().initDeferredImageLoader(imageLoaderClass);
    }

}
