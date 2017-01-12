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
