# RxFacebook
[![Build Status](https://travis-ci.org/ChristianGarcia/RxFacebook.svg?branch=master)](https://travis-ci.org/ChristianGarcia/RxFacebook)
[![](https://img.shields.io/maven-central/v/com.christiangp/RxFacebook.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.christiangp%22%20a%3A%22RxFacebook%22)

RxJava Binding APIs for Facebook's Android SDK.

This is currently in experimental phase. Use with care.

## Download
```groovy
compile 'com.christiangp:RxFacebook:<latest-version>'
```

## Usage

### Signing in

Subscribe to `RxFacebook.logIn()`.

Make sure `RxFacebook.logIn()` is done in the UI Thread (typically by observing on `mainThread()` before it)

The generated `Observable` holds a strong reference to the host `Activity`. 
Remember to dispose the subscription `Disposable` once you're done with it (typically in the `onDestroy()` method) to free this reference.

That's it. No need to handle any `onActivityResult()`.

```java
Disposable loginDisposable =
  RxFacebook.logIn(activity, permissions)
            .subscribe(
              result -> {
                if (result instanceof LoginResultCanceledEvent) {
                  //Do something on canceled
                } else if (result instanceof LoginResultSuccessfulEvent) {
                  final LoginResult loginResult = ((LoginResultSuccessfulEvent) result).loginResult();
                  //Do something with loginResult on success
                }
              },
              throwable -> {
                //Do something on error
              }
            );
```

### Graph API requests
Subscribe to `RxFacebook.graphRequest()`

```java
final Bundle params = new Bundle();
params.putString("fields", 
  "id, birthday, first_name, gender, last_name, link, location, locale, name, timezone, updated_time, email"
);

RxFacebook.graphRequest(AccessToken.getCurrentAccessToken(), "me", params, HttpMethod.GET)
          .subscribe(graphResponse -> {
              // Do somehitng with graphResponse
          });
```

## License

    Copyright (c) 2017 Christian García
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
        http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
