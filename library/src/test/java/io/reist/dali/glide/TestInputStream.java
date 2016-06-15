package io.reist.dali.glide;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Reist on 15.06.16.
 */
class TestInputStream extends InputStream {

    private final String model;

    TestInputStream(String model) {
        this.model = model;
    }

    @Override
    public int read() throws IOException {
        return 0;
    }

    String getModel() {
        return model;
    }

}
