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

import com.christiangp.rxfacebook.LoginResultCanceledEvent;
import com.christiangp.rxfacebook.LoginResultEvent;
import com.christiangp.rxfacebook.LoginResultSuccessfulEvent;

class SignInMapper {

    static SignInResult mapToResult(LoginResultEvent loginResultEvent) {
        if (loginResultEvent instanceof LoginResultCanceledEvent) {
            return SignInResult.Canceled.create();
        } else if (loginResultEvent instanceof LoginResultSuccessfulEvent) {
            return SignInResult.Success.create(((LoginResultSuccessfulEvent) loginResultEvent).loginResult());
        }
        throw new IllegalArgumentException("Unsupported event: " + loginResultEvent.getClass());
    }
}
