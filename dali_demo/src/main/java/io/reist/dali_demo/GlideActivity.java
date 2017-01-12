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

package io.reist.dali_demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import io.reist.dali.Dali;
import io.reist.dali.glide.GlideImageLoader;

/**
 * Created by Reist on 14.06.16.
 */
public class GlideActivity extends DemoActivity {

    public GlideActivity() {
        super(GlideImageLoader.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setTitle(R.string.activity_glide);

        recyclerView.setAdapter(new ImageListAdapter() {

            @Override
            protected String getUrl(int i) {
                return "file:///android_asset/image.jpg";
            }

        });

        //recyclerView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(GlideActivity.this, MainActivity.class));
        return true;
    }

}
