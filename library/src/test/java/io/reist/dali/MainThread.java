package io.reist.dali;

import org.robolectric.shadows.ShadowLooper;

/**
 * Created by Reist on 15.06.16.
 */
public class MainThread {

    private final Object lock = new Object();

    private volatile boolean locked;

    public void loop() {

        long startTime = System.currentTimeMillis();

        synchronized (lock) {
            locked = true;
            while (locked && (System.currentTimeMillis() - startTime) < 3000) {
                ShadowLooper.idleMainLooper();
                TestUtils.delay(100);
            }
        }

    }

    public void stop() {
        synchronized (lock) {
            locked = false;
        }
    }

}
