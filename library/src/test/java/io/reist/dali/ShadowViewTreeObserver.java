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
