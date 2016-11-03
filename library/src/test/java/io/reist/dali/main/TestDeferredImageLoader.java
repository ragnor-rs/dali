package io.reist.dali.main;

import android.view.View;

import org.mockito.Mockito;

import io.reist.dali.DaliLoader;
import io.reist.dali.DeferredImageLoader;

/**
 * Created by Reist on 15.06.16.
 */
public class TestDeferredImageLoader extends DeferredImageLoader {

    private final DeferredImageLoader dummy = Mockito.mock(DeferredImageLoader.class);

    @Override
    public void defer(View view, ViewRequestFactory viewRequestFactory) {
        super.defer(view, viewRequestFactory);
        dummy.defer(view, viewRequestFactory);
    }

    public void assertLoadingDeferred(View targetView, boolean shouldCall) {
        Mockito.verify(
                ((TestDeferredImageLoader) DaliLoader.getInstance().getDeferredImageLoader()).dummy,
                Mockito.times(shouldCall ? 1 : 0)
        ).defer(
                Mockito.eq(targetView),
                Mockito.any(DeferredImageLoader.ViewRequestFactory.class)
        );
    }

}
