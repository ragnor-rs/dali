package io.reist.dali_demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;

import io.reist.dali.Dali;
import io.reist.dali.ImageLoader;

public class DemoActivity extends AppCompatActivity {

    protected RecyclerView recyclerView;

    private final Class<? extends ImageLoader> imageLoaderClass;

    public DemoActivity(Class<? extends ImageLoader> imageLoaderClass) {
        this.imageLoaderClass = imageLoaderClass;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Dali.setMainImageLoaderClass(imageLoaderClass);
        Dali.setDebuggable(true);

        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        if (recyclerView == null) {
            throw new IllegalStateException("Activity layout must contain a recycler view");
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.demo, menu);
        return true;
    }

}
