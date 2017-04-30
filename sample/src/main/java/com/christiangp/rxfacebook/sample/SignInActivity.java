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
package com.christiangp.rxfacebook.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.christiangp.rxfacebook.RxFacebook;
import com.facebook.AccessToken;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class SignInActivity
    extends AppCompatActivity {

    @BindView(R.id.contentAnimator)
    ViewAnimator contentAnimator;

    @BindView(R.id.facebookButton)
    Button       facebookButton;

    @BindView(R.id.signedInView)
    LinearLayout signedInView;

    @BindView(R.id.userIdView)
    TextView     userIdView;

    @BindView(R.id.accessTokenView)
    TextView     accessTokenView;

    private CompositeDisposable disposables;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
        contentAnimator.setDisplayedChild(contentAnimator.indexOfChild(facebookButton));
        disposables = new CompositeDisposable(
            RxView.clicks(facebookButton)
                  .throttleLast(1, TimeUnit.SECONDS)
                  .observeOn(AndroidSchedulers.mainThread())
                  .flatMap(__ -> RxFacebook.logIn(this, Arrays.asList("email")))
                  .map(SignInMapper::mapToResult)
                  .onErrorReturn(SignInResult.Failure::create)
                  .subscribe(
                      result -> {
                          if (result instanceof SignInResult.Canceled) {
                              Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT)
                                   .show();
                          } else if (result instanceof SignInResult.Success) {
                              contentAnimator.setDisplayedChild(contentAnimator.indexOfChild(signedInView));
                              final AccessToken accessToken = ((SignInResult.Success) result).loginResult()
                                                                                             .getAccessToken();
                              userIdView.setText(accessToken.getUserId());
                              accessTokenView.setText(accessToken.getToken());
                          } else if (result instanceof SignInResult.Failure) {
                              final SignInResult.Failure failure = (SignInResult.Failure) result;
                              @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
                              final Throwable error = failure.error();
                              Toast.makeText(this, "Sign in failed:" + error.getLocalizedMessage(), Toast.LENGTH_SHORT)
                                   .show();
                          }
                      }
                  )
        );
    }

    @Override
    protected void onDestroy() {
        disposables.dispose();
        super.onDestroy();
    }
}
