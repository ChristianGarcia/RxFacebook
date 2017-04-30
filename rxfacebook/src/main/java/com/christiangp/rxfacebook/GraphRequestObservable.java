/*
 * Copyright (c) 2017 Christian García
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
package com.christiangp.rxfacebook;

import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

final class GraphRequestObservable
    extends Observable<GraphResponse> {

    private final AccessToken accessToken;

    private final String      graphPath;

    private final Bundle      parameters;

    private final HttpMethod  httpMethod;

    GraphRequestObservable(
        AccessToken accessToken,
        String graphPath,
        Bundle parameters,
        HttpMethod httpMethod
    ) {
        this.accessToken = accessToken;
        this.graphPath = graphPath;
        this.parameters = parameters;
        this.httpMethod = httpMethod;
    }

    @Override
    protected void subscribeActual(Observer<? super GraphResponse> observer) {
        final Listener listener = new Listener(observer);
        observer.onSubscribe(listener);

        new GraphRequest(
            accessToken,
            graphPath,
            parameters,
            httpMethod,
            listener
        ).executeAsync();
    }

    private static class Listener
        extends MainThreadDisposable
        implements GraphRequest.Callback {

        private final Observer<? super GraphResponse> observer;

        Listener(Observer<? super GraphResponse> observer) {
            this.observer = observer;
        }

        @Override
        public void onCompleted(GraphResponse response) {
            observer.onNext(response);
            observer.onComplete();
        }

        @Override
        protected void onDispose() {
            //Nothing to clear
        }
    }
}
