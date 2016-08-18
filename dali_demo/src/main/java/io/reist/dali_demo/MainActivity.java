package io.reist.dali_demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

public class MainActivity extends DemoActivity {

    public MainActivity() {
        super(ImageServiceLoader.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setTitle(R.string.activity_main);

        recyclerView.setAdapter(new ImageListAdapter() {

            @Override
            protected String getUrl(int i) {
                return ImageService.positionToUrl(i);
            }

        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(MainActivity.this, GlideActivity.class));
        return true;
    }

}
