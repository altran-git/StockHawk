package com.altran.android.stockhawk;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by ND on 6/30/2016.
 */
public class MyApplication extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    Stetho.initializeWithDefaults(this);
  }
}
