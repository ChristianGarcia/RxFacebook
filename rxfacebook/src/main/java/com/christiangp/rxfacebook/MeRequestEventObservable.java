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

import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

class MeRequestEventObservable
    extends Observable<GraphResponseEvent> {

    private final AccessToken accessToken;

    private final Bundle      parameters;

    MeRequestEventObservable(AccessToken accessToken, Bundle parameters) {
        this.accessToken = accessToken;
        this.parameters = parameters;
    }

    @Override
    protected void subscribeActual(Observer<? super GraphResponseEvent> observer) {
        Listener listener = new Listener(observer);
        observer.onSubscribe(listener);

        final GraphRequest request = GraphRequest.newMeRequest(
            accessToken,
            listener);
        request.setParameters(parameters);
        request.executeAsync();
    }

    private static class Listener
        extends MainThreadDisposable
        implements GraphRequest.GraphJSONObjectCallback {

        private final Observer<? super GraphResponseEvent> observer;

        Listener(Observer<? super GraphResponseEvent> observer) {
            this.observer = observer;
        }

        @Override
        public void onCompleted(JSONObject object, GraphResponse response) {
            observer.onNext(GraphResponseEvent.create(response));
            observer.onComplete();
        }

        @Override
        protected void onDispose() {
            //Nothing to clear
        }
    }
}
