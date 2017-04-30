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
import android.widget.TextView;

import com.christiangp.rxfacebook.RxFacebook;
import com.facebook.AccessToken;
import com.facebook.HttpMethod;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MeRequestActivity
    extends AppCompatActivity {

    @BindView(R.id.rawResponseView)
    TextView rawResponseView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_request);
        ButterKnife.bind(this);

        Bundle params = new Bundle();
        params.putString("fields", "id, birthday, first_name, gender, last_name, link, location, locale, name, timezone, updated_time, email");

        RxFacebook.graphRequest(AccessToken.getCurrentAccessToken(), "me", params, HttpMethod.GET)
                  .subscribe(graphResponse -> {
                      if (graphResponse.getError() != null) {
                          rawResponseView.setText(graphResponse.getError()
                                                               .getErrorMessage());
                      } else {
                          rawResponseView.setText(graphResponse.getRawResponse());
                      }
                  });
    }
}
