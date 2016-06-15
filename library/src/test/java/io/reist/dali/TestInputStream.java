package io.reist.dali;

import java.io.IOException;
import java.io.InputStream;



/**
 * Created by Reist on 15.06.16.
 */
public class TestInputStream extends InputStream {
    private final String model;

    public TestInputStream(String model) {
        this.model = model;
    }

    @Override
    public int read() throws IOException {
        return 0;
    }

    public String getModel() {
        return model;
    }
}
