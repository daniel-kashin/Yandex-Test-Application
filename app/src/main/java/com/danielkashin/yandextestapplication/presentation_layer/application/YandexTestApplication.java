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
    ITranslationsLocalService localInstance = translateLocalService;
    if (localInstance == null) {
      synchronized (ITranslationsLocalService.class) {
        localInstance = translateLocalService;
        if (localInstance == null) {
          translateLocalService = localInstance = TranslationsLocalService.Factory
              .create(SQLiteFactory.create(this));
        }
      }
    }

    return localInstance;
  }

}
