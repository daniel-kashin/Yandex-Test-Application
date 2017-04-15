package com.danielkashin.yandextestapplication.presentation_layer.application;

import android.app.Application;

import com.danielkashin.yandextestapplication.data_layer.database.SQLiteFactory;
import com.danielkashin.yandextestapplication.data_layer.services.translate.local.ITranslateLocalService;
import com.danielkashin.yandextestapplication.data_layer.services.translate.local.TranslateLocalService;
import com.squareup.leakcanary.LeakCanary;


public class YandexTestApplication extends Application implements ITranslateLocalServiceProvider {

  private static volatile ITranslateLocalService translateLocalService;


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
  public ITranslateLocalService getTranslateLocalService(){
    ITranslateLocalService localInstance = translateLocalService;
    if (localInstance == null) {
      synchronized (ITranslateLocalService.class) {
        localInstance = translateLocalService;
        if (localInstance == null) {
          translateLocalService = localInstance = TranslateLocalService.Factory
              .create(SQLiteFactory.create(this));
        }
      }
    }

    return localInstance;
  }

}
