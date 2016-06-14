package io.reist.dali;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Emulates {@link android.os.Handler}.
 */
class ShadowHandler {

    private final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(16);

    private final static ShadowHandler INSTANCE = new ShadowHandler();

    public ShadowHandler() {
        (new Thread() {

            @Override
            public void run() {
                try {
                    Runnable r;
                    while ((r = queue.take()) != null) {
                        r.run();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        }).start();
    }

    public static ShadowHandler getInstance() {
        return INSTANCE;
    }

    public void post(Runnable runnable) {
        queue.offer(runnable);
    }

}
