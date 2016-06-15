package io.reist.dali;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Emulates {@link android.os.Handler}.
 */
class RunnableQueue {

    private final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(16);

    private final static RunnableQueue INSTANCE = new RunnableQueue();

    public RunnableQueue() {
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

    public static RunnableQueue getInstance() {
        return INSTANCE;
    }

    public void post(Runnable runnable) {
        queue.offer(runnable);
    }

}
