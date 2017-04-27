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
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Collections;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

import static com.christiangp.rxfacebook.internal.Preconditions.checkMainThread;

final class FacebookResultEventObservable
    extends Observable<FacebookSignInResultEvent> {

    private static final String FRAGMENT_TAG = "RxFacebookFragment";

    private Activity activity;

    public FacebookResultEventObservable(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void subscribeActual(Observer<? super FacebookSignInResultEvent> observer) {
        if (!checkMainThread(observer)) {
            return;
        }
        final CallbackManager facebookCallbackManager = CallbackManager.Factory.create();

        RxFacebookFragment rxFacebookFragment = (RxFacebookFragment) activity.getFragmentManager()
                                                                             .findFragmentByTag(FRAGMENT_TAG);
        if (rxFacebookFragment != null) {
            activity.getFragmentManager()
                    .beginTransaction()
                    .remove(rxFacebookFragment)
                    .commitAllowingStateLoss();
        }
        rxFacebookFragment = RxFacebookFragment.newInstance();
        rxFacebookFragment.setFacebookCallbackManager(facebookCallbackManager);
        activity.getFragmentManager()
                .beginTransaction()
                .add(rxFacebookFragment, FRAGMENT_TAG)
                .commitAllowingStateLoss();

        final Listener listener = new Listener(this, observer);
        LoginManager.getInstance()
                    .registerCallback(facebookCallbackManager, listener);

        observer.onSubscribe(listener);
    }

    @SuppressWarnings("WeakerAccess")
    void release() {
        activity = null;
    }

    static class Listener
        extends MainThreadDisposable
        implements FacebookCallback<LoginResult> {

        private final FacebookResultEventObservable               requester;

        private final Observer<? super FacebookSignInResultEvent> observer;

        public Listener(FacebookResultEventObservable requester, Observer<? super FacebookSignInResultEvent> observer) {
            this.requester = requester;
            this.observer = observer;
        }

        @Override
        public void onSuccess(LoginResult loginResult) {
            if (!isDisposed()) {
                observer.onNext(FacebookSignInSuccessEvent.create(loginResult));
                observer.onComplete();
            }
        }

        @Override
        public void onCancel() {
            if (!isDisposed()) {
                observer.onNext(FacebookSignInResultCanceledEvent.create());
                observer.onComplete();
            }
        }

        @Override
        public void onError(FacebookException error) {
            if (!isDisposed()) {
                observer.onError(error);
            }
        }

        @Override
        protected void onDispose() {
            requester.release();
        }
    }

    public static class RxFacebookFragment
        extends Fragment {

        private CallbackManager facebookCallbackManager;

        public static RxFacebookFragment newInstance() {
            final Bundle args = new Bundle();

            final RxFacebookFragment fragment = new RxFacebookFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
            LoginManager.getInstance()
                        .logInWithReadPermissions(this, Collections.singletonList("public_profile"));
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        }

        public void setFacebookCallbackManager(CallbackManager facebookCallbackManager) {
            this.facebookCallbackManager = facebookCallbackManager;
        }
    }
}
