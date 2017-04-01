package com.danielkashin.yandextestapplication.data_layer.services.base;

import com.danielkashin.yandextestapplication.BuildConfig;
import io.realm.Realm;
import io.realm.RealmConfiguration;


public class DatabaseService {

  private RealmConfiguration configuration;

  public DatabaseService() {
    configuration = new RealmConfiguration.Builder()
        .name(BuildConfig.REALM_CONFIGURATION_NAME)
        .deleteRealmIfMigrationNeeded()
        .schemaVersion(1)
        .build();
  }

  protected Realm getService() {
    return Realm.getInstance(configuration);
  }

}
