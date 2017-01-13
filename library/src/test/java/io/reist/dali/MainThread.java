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
