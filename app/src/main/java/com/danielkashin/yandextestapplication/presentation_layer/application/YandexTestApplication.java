package com.danielkashin.yandextestapplication.presentation_layer.application;

import android.app.Application;

import com.danielkashin.yandextestapplication.data_layer.database.SQLiteFactory;
import com.danielkashin.yandextestapplication.data_layer.services.translate.local.ITranslationsLocalService;
import com.danielkashin.yandextestapplication.data_layer.services.translate.local.TranslationsLocalService;


public class YandexTestApplication extends Application implements ITranslateLocalServiceProvider {

  private static volatile ITranslationsLocalService translateLocalService;


  @Override
  public void onCreate() {
    super.onCreate();
  }

  @Override
  public ITranslationsLocalService getTranslateLocalService() {
    // double check lock is thread safety and lazy initialization to not freeze app on start
    // getting service from activity`s interface is better than singleton for two reasons:
    // 1. single responsibility principle, that says that some class better not to
    // be responsible for representing some struct and containing exactly one instance of its struct
    // 2. data injection of more clear and obvious in case of calling some provider rather than
    // static method with public modifier available from any context

    ITranslationsLocalService localInstance = translateLocalService;
    if (localInstance == null) {
      synchronized (ITranslationsLocalService.class) {
        localInstance = translateLocalService;
        if (localInstance == null) {
          translateLocalService = localInstance = TranslationsLocalService.Factory
              .create(SQLiteFactory.create(getApplicationContext()));
        }
      }
    }
    return localInstance;
  }

}
