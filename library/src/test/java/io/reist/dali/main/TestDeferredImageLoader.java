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
