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

import com.facebook.login.LoginResult;
import com.google.auto.value.AutoValue;

// Missing you here, Kotlin. :sigh:
interface SignInResult {

    @AutoValue
    abstract class Canceled
        implements SignInResult {

        static SignInResult create() {
            return new AutoValue_SignInResult_Canceled();
        }
    }

    @AutoValue
    abstract class Success
        implements SignInResult {

        static SignInResult create(LoginResult loginResult) {
            return new AutoValue_SignInResult_Success(loginResult);
        }

        abstract LoginResult loginResult();
    }

    @AutoValue
    abstract class Failure
        implements SignInResult {

        static SignInResult create(Throwable error) {
            return new AutoValue_SignInResult_Failure(error);
        }

        abstract Throwable error();
    }
}
