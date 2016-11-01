package io.reist.dali_demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

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

        recyclerView.setAdapter(new ImageListAdapter(R.layout.glide_item) {

            @Override
            protected String getUrl(int i) {
                return "https://www.petfinder.com/wp-content/uploads/2012/11/140272627-grooming-needs-senior-cat-632x475.jpg";
            }

        });

        recyclerView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(GlideActivity.this, MainActivity.class));
        return true;
    }

}
