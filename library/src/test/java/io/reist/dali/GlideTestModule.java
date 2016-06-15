package io.reist.dali;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.MemoryCache;
import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.module.GlideModule;

import org.mockito.Mockito;

import java.io.InputStream;
import java.util.concurrent.Executors;

/**
 * Created by Reist on 15.06.16.
 */
public class GlideTestModule implements GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        builder
                .setDiskCache(new DiskCache.Factory() {

                    @Override
                    public DiskCache build() {
                        return Mockito.mock(DiskCache.class);
                    }

                })
                .setMemoryCache(Mockito.mock(MemoryCache.class))
                .setResizeService(Executors.newFixedThreadPool(1))
                .setDiskCacheService(Executors.newFixedThreadPool(1));

    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        glide.register(
                String.class,
                InputStream.class,
                new ModelLoaderFactory<String, InputStream>() {

                    @Override
                    public ModelLoader<String, InputStream> build(Context context, GenericLoaderFactory factories) {
                        return new ModelLoader<String, InputStream>() {

                            @Override
                            public DataFetcher<InputStream> getResourceFetcher(final String model, int width, int height) {
                                System.out.println("getResourceFetcher(" + model + ")");
                                return new DataFetcher<InputStream>() {

                                    @Override
                                    public InputStream loadData(Priority priority) throws Exception {
                                        return new TestInputStream(model);
                                    }

                                    @Override
                                    public void cleanup() {}

                                    @Override
                                    public String getId() {
                                        return model;
                                    }

                                    @Override
                                    public void cancel() {}

                                };
                            }

                        };
                    }

                    @Override
                    public void teardown() {}

                }
        );
    }

}
