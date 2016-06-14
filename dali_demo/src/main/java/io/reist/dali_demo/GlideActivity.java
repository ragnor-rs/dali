package io.reist.dali_demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import io.reist.dali.GlideImageLoader;

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
                return "http://lorempixel.com/500/500/?v=" + Math.random();
            }

        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(GlideActivity.this, MainActivity.class));
        return true;
    }

}
