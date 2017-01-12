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

import android.security.NetworkSecurityPolicy;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

/**
 * Created by Reist on 08.11.16.
 */
@SuppressWarnings("unused")
@Implements(NetworkSecurityPolicy.class)
public class ShadowNetworkSecurityPolicy {

    @Implementation
    public static NetworkSecurityPolicy getInstance() {
        try {
            Class<?> shadow = Class.forName("android.security.NetworkSecurityPolicy");
            return (NetworkSecurityPolicy) shadow.newInstance();
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    @Implementation
    public boolean isCleartextTrafficPermitted(String hostname) {
        return true;
    }

}
