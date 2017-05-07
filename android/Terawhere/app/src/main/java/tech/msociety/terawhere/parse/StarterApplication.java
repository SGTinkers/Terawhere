/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package tech.msociety.terawhere.parse;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;


public class StarterApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    // Enable Local Datastore.
    Parse.enableLocalDatastore(this);

    // Add your initialization code here
    Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
            .applicationId("ea4abc9cfdad3e266f95844d9a570e67e4314445")
            .clientKey("c6908bca062dbda3da8449499618845b9cbde220")
            .server("http://ec2-54-214-65-140.us-west-2.compute.amazonaws.com:80/parse/")
            .build()
    );
    ParseObject object = new ParseObject("ExampleObject");
    object.put("myNumber", "123");
    object.put("myString", "rob");


   // ParseUser.enableAutomaticUser();

    ParseACL defaultACL = new ParseACL();
    defaultACL.setPublicReadAccess(true);
    defaultACL.setPublicWriteAccess(true);
    ParseACL.setDefaultACL(defaultACL, true);

  }
}
