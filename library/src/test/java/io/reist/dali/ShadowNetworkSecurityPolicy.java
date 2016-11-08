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
