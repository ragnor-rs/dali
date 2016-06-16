package io.reist.dali_demo;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

/**
 * Created by m039 on 11/30/15.
 */
public class ActivityInstrumentationTestCase<T extends Activity> extends ActivityInstrumentationTestCase2<T> {

    public ActivityInstrumentationTestCase(Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void setUp() throws Exception {

        super.setUp();

        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        injectInstrumentation(instrumentation);

        synchronized (ActivityInstrumentationTestCase.class) {}

    }

}