package io.reist.dali.glide;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

/**
 * Created by Reist on 15.06.16.
 */
class GlideTestApp extends Application {

    @Override
    public void onCreate() {

        super.onCreate();

        ApplicationInfo applicationInfo;
        try {
            applicationInfo = getPackageManager().getApplicationInfo(
                    getPackageName(),
                    PackageManager.GET_META_DATA
            );
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        applicationInfo.metaData.remove(GlideImageLoaderModule.class.getName());
        applicationInfo.metaData.putString(GlideTestModule.class.getName(), "GlideModule");

    }

}
