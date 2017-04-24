package com.danielkashin.yandextestapplication.data_layer.services.base;

import com.pushtorefresh.storio.sqlite.StorIOSQLite;


public class DatabaseService {

  private StorIOSQLite sqLite;

  protected DatabaseService(StorIOSQLite sqLite){
    this.sqLite = sqLite;
  }

  protected StorIOSQLite getSQLite(){
    return sqLite;
  }
}
