package com.danielkashin.yandextestapplication.data_layer.services.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.danielkashin.yandextestapplication.data_layer.services.base.DatabaseService;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;


public class TranslateLocalService extends DatabaseService implements ITranslateLocalService {

  public TranslateLocalService(StorIOSQLite sqLite){
    super(sqLite);
  }



  public static class Factory {

    private Factory() {}

    public static ITranslateLocalService create(StorIOSQLite sqLite){
      return new TranslateLocalService(sqLite);
    }

  }

}
