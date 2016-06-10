package io.reist.dali_demo;

import android.app.Application;

import io.reist.dali.Dali;

/**
 * Created by Reist on 10.06.16.
 */
public class MainApplication extends Application {

    static {
        Dali.setMainImageLoaderClass(ImageServiceLoader.class);
    }

}
