package com.danielkashin.yandextestapplication.data_layer.services.base;

import com.danielkashin.yandextestapplication.BuildConfig;
import io.realm.Realm;
import io.realm.RealmConfiguration;


public class DatabaseService {

  private RealmConfiguration configuration;
  private Realm realm;

  public DatabaseService() {
    configuration = new RealmConfiguration.Builder()
        .name(BuildConfig.REALM_CONFIGURATION_NAME)
        .deleteRealmIfMigrationNeeded()
        .schemaVersion(1)
        .build();

    realm = Realm.getInstance(configuration);
  }

  protected Realm getService() {
    return realm;
  }

}
