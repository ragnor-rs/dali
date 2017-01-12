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

import android.view.ViewTreeObserver;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Taken from
 * https://github.com/bumptech/glide/blob/3c3bcc21a0e8adf596bc9f714286943565aab719/library/src/test/java/com/bumptech/glide/request/target/ViewTargetTest.java#L406
 */
@Implements(ViewTreeObserver.class)
public class ShadowViewTreeObserver {

    private final CopyOnWriteArrayList<ViewTreeObserver.OnPreDrawListener> preDrawListeners = new CopyOnWriteArrayList<>();

    private boolean isAlive = true;

    @SuppressWarnings("unused")
    @Implementation
    public void addOnPreDrawListener(ViewTreeObserver.OnPreDrawListener listener) {
        checkIsAlive();
        preDrawListeners.add(listener);
    }

    @SuppressWarnings("unused")
    @Implementation
    public void removeOnPreDrawListener(ViewTreeObserver.OnPreDrawListener listener) {
        checkIsAlive();
        preDrawListeners.remove(listener);
    }

    @Implementation
    public boolean isAlive() {
        return isAlive;
    }

    private void checkIsAlive() {
        if (!isAlive()) {
            throw new IllegalStateException("ViewTreeObserver is not alive!");
        }
    }

    @SuppressWarnings("unused")
    public void setIsAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    @SuppressWarnings("unused")
    public void fireOnPreDrawListeners() {
        for (ViewTreeObserver.OnPreDrawListener listener : preDrawListeners) {
            listener.onPreDraw();
        }
    }

    @SuppressWarnings("unused")
    public List<ViewTreeObserver.OnPreDrawListener> getPreDrawListeners() {
        return preDrawListeners;
    }

}
