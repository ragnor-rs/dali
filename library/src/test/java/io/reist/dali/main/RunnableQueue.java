package io.reist.dali.main;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Emulates {@link android.os.Handler}.
 */
class RunnableQueue {

    private final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(16);

    private final static RunnableQueue INSTANCE = new RunnableQueue();

    RunnableQueue() {
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

    static RunnableQueue getInstance() {
        return INSTANCE;
    }

    void post(Runnable runnable) {
        queue.offer(runnable);
    }

}
