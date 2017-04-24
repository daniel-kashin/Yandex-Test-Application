package com.danielkashin.yandextestapplication.data_layer.services.base;

import com.pushtorefresh.storio.sqlite.StorIOSQLite;

/*
* StorIO + SQLite one love
* I dislike Realm`s architecture
* generic service is helpful if out application will become bigger and ther services will be required
*/
public class DatabaseService {

  private StorIOSQLite sqLite;

  protected DatabaseService(StorIOSQLite sqLite){
    this.sqLite = sqLite;
  }

  protected StorIOSQLite getSQLite(){
    return sqLite;
  }
}
