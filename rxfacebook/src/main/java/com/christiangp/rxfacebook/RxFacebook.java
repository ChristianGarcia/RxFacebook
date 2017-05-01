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

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.facebook.AccessToken;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import java.util.Collection;

import io.reactivex.Observable;

public final class RxFacebook {

    /**
     * Create an observable which emits Facebook Login events. The emitted value is a subclass of {@link LoginResultEvent}
     * <p>
     * The Facebook Login process is executed on subscribing to this observable.
     * <p>
     * <b><i>WARNING</i></b>: The created observable keeps a strong reference to the given Activity. Unsubscribe to free this reference.
     *
     * @param activity    The host Activity.
     * @param permissions The Facebook Login <a href="https://developers.facebook.com/docs/facebook-login/permissions">permissions</a> scope.
     */
    @NonNull
    public static Observable<LoginResultEvent> logIn(@NonNull Activity activity, @NonNull Collection<String> permissions) {
        return new LoginResultEventObservable(activity, permissions);
    }

    /**
     * Create an observable which emits {@link GraphResponse}s as events.
     * <p>
     * Check the official <a href="https://developers.facebook.com/docs/graph-api/reference">reference documentation</a> for each request's HTTP method and parameters.
     *
     * @param accessToken An access token obtained with the necessary permissions for this Graph call
     * @param graphPath   HTTP path
     * @param parameters  Optional parameters
     * @param httpMethod  HTTP Method
     */
    @NonNull
    public static Observable<GraphResponse> graphRequest(
        @NonNull AccessToken accessToken,
        @NonNull String graphPath,
        @Nullable Bundle parameters,
        @NonNull HttpMethod httpMethod
    ) {
        return new GraphRequestObservable(accessToken, graphPath, parameters, httpMethod);
    }
}
