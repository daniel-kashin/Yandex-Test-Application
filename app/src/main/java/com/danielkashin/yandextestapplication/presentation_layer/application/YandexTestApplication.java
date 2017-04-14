package com.danielkashin.yandextestapplication.presentation_layer.application;

import android.app.Application;

import com.danielkashin.yandextestapplication.data_layer.database.SQLiteFactory;
import com.danielkashin.yandextestapplication.data_layer.services.translation.local.ITranslationLocalService;
import com.danielkashin.yandextestapplication.data_layer.services.translation.local.TranslationLocalService;
import com.squareup.leakcanary.LeakCanary;


public class YandexTestApplication extends Application implements ITranslateLocalServiceProvider {

  private static volatile ITranslationLocalService translateLocalService;


  @Override
  public void onCreate(){
    super.onCreate();
    if (LeakCanary.isInAnalyzerProcess(this)) {
      // do nothing
    } else {
      LeakCanary.install(this);
    }
  }

  @Override
  public ITranslationLocalService getTranslateLocalService(){
    ITranslationLocalService localInstance = translateLocalService;
    if (localInstance == null) {
      synchronized (ITranslationLocalService.class) {
        localInstance = translateLocalService;
        if (localInstance == null) {
          translateLocalService = localInstance = TranslationLocalService.Factory
              .create(SQLiteFactory.create(this));
        }
      }
    }

    return localInstance;
  }

}
