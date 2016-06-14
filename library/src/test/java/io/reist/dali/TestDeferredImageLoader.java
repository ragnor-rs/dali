package io.reist.dali;

import android.view.View;

import org.mockito.Mockito;

/**
 * Created by Reist on 15.06.16.
 */
class TestDeferredImageLoader extends DeferredImageLoader {

    private final DeferredImageLoader dummy = Mockito.mock(DeferredImageLoader.class);

    @Override
    protected void defer(View view, ViewRequestFactory viewRequestFactory) {
        super.defer(view, viewRequestFactory);
        dummy.defer(view, viewRequestFactory);
    }

    public void assertLoadingDeferred(View targetView, boolean shouldCall) {
        Mockito.verify(
                ((TestDeferredImageLoader) Dali.getInstance().getDeferredImageLoader()).dummy,
                Mockito.times(shouldCall ? 1 : 0)
        ).defer(
                Mockito.eq(targetView),
                Mockito.any(DeferredImageLoader.ViewRequestFactory.class)
        );
    }

}
