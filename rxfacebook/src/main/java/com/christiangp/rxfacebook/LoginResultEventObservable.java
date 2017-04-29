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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.ArrayList;
import java.util.Collection;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

final class LoginResultEventObservable
    extends Observable<LoginResultEvent> {

    private static final String FRAGMENT_TAG = "RxFacebookFragment";

    private final Collection<String> permissions;

    private       Activity           activity;

    LoginResultEventObservable(Activity activity, Collection<String> permissions) {
        this.activity = activity;
        this.permissions = permissions;
    }

    @Override
    protected void subscribeActual(Observer<? super LoginResultEvent> observer) {
        MainThreadDisposable.verifyMainThread();
        
        final CallbackManager facebookCallbackManager = CallbackManager.Factory.create();

        RxFacebookFragment rxFacebookFragment = (RxFacebookFragment) activity.getFragmentManager()
                                                                             .findFragmentByTag(FRAGMENT_TAG);
        if (rxFacebookFragment != null) {
            activity.getFragmentManager()
                    .beginTransaction()
                    .remove(rxFacebookFragment)
                    .commitAllowingStateLoss();
        }
        rxFacebookFragment = RxFacebookFragment.newInstance(permissions);
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

    private static class Listener
        extends MainThreadDisposable
        implements FacebookCallback<LoginResult> {

        private final LoginResultEventObservable         requester;

        private final Observer<? super LoginResultEvent> observer;

        Listener(LoginResultEventObservable requester, Observer<? super LoginResultEvent> observer) {
            this.requester = requester;
            this.observer = observer;
        }

        @Override
        public void onSuccess(LoginResult loginResult) {
            if (!isDisposed()) {
                observer.onNext(LoginSuccessEvent.create(loginResult));
                observer.onComplete();
            }
        }

        @Override
        public void onCancel() {
            if (!isDisposed()) {
                observer.onNext(LoginResultCanceledEvent.create());
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

        private static final String KEY_PERMISSIONS = "permissions";

        private CallbackManager facebookCallbackManager;

        public static RxFacebookFragment newInstance(@NonNull Collection<String> permissions) {
            final Bundle args = new Bundle();
            args.putStringArrayList(KEY_PERMISSIONS, new ArrayList<>(permissions));

            final RxFacebookFragment fragment = new RxFacebookFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
            LoginManager.getInstance()
                        .logInWithReadPermissions(this, getArguments().getStringArrayList(KEY_PERMISSIONS));
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
