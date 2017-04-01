package com.danielkashin.yandextestapplication.presentation_layer.application;

import android.app.Application;
import io.realm.Realm;


public class YandexTestApplication extends Application {

  @Override
  public void onCreate(){
    super.onCreate();
    Realm.init(this);
  }

}
