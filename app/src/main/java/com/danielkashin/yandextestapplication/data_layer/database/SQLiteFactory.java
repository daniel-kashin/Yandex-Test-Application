package com.danielkashin.yandextestapplication.data_layer.database;

import android.content.Context;
import com.danielkashin.yandextestapplication.data_layer.entities.local.DatabaseTranslation;
import com.danielkashin.yandextestapplication.data_layer.entities.local.DatabaseTranslationStorIOSQLiteDeleteResolver;
import com.danielkashin.yandextestapplication.data_layer.entities.local.DatabaseTranslationStorIOSQLiteGetResolver;
import com.danielkashin.yandextestapplication.data_layer.entities.local.DatabaseTranslationStorIOSQLitePutResolver;
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;


public class SQLiteFactory {

  private SQLiteFactory() {}


  public static StorIOSQLite create(Context context){
    return DefaultStorIOSQLite.builder()
        .sqliteOpenHelper(new DatabaseOpenHelper(context))
        .addTypeMapping(DatabaseTranslation.class, SQLiteTypeMapping.<DatabaseTranslation>builder()
          .putResolver(new DatabaseTranslationStorIOSQLitePutResolver())
          .getResolver(new DatabaseTranslationStorIOSQLiteGetResolver())
          .deleteResolver(new DatabaseTranslationStorIOSQLiteDeleteResolver())
        .build())
        .build();
  }

}
