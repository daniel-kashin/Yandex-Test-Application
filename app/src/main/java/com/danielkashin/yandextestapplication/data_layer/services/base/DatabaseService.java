package com.danielkashin.yandextestapplication.data_layer.services.base;

import com.danielkashin.yandextestapplication.BuildConfig;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class DatabaseService {

  private StorIOSQLite sqLite;

  protected DatabaseService(StorIOSQLite sqLite){
    this.sqLite = sqLite;
  }

  protected StorIOSQLite getSQLite(){
    return sqLite;
  }
}
