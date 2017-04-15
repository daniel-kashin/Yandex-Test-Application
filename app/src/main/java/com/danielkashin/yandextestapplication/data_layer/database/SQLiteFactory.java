package com.danielkashin.yandextestapplication.data_layer.database;

import android.content.Context;
import com.danielkashin.yandextestapplication.data_layer.entities.translate.local.DatabaseLanguage;
import com.danielkashin.yandextestapplication.data_layer.entities.translate.local.DatabaseLanguageStorIOSQLiteDeleteResolver;
import com.danielkashin.yandextestapplication.data_layer.entities.translate.local.DatabaseLanguageStorIOSQLiteGetResolver;
import com.danielkashin.yandextestapplication.data_layer.entities.translate.local.DatabaseLanguageStorIOSQLitePutResolver;
import com.danielkashin.yandextestapplication.data_layer.entities.translate.local.DatabaseTranslation;
import com.danielkashin.yandextestapplication.data_layer.entities.translate.local.DatabaseTranslationStorIOSQLiteDeleteResolver;
import com.danielkashin.yandextestapplication.data_layer.entities.translate.local.DatabaseTranslationStorIOSQLiteGetResolver;
import com.danielkashin.yandextestapplication.data_layer.entities.translate.local.DatabaseTranslationStorIOSQLitePutResolver;
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;


public class SQLiteFactory {

  private SQLiteFactory() {
  }

  public static StorIOSQLite create(Context context) {
    return DefaultStorIOSQLite.builder()
        .sqliteOpenHelper(new DatabaseOpenHelper(context))
        .addTypeMapping(DatabaseTranslation.class, SQLiteTypeMapping.<DatabaseTranslation>builder()
            .putResolver(new DatabaseTranslationStorIOSQLitePutResolver())
            .getResolver(new DatabaseTranslationStorIOSQLiteGetResolver())
            .deleteResolver(new DatabaseTranslationStorIOSQLiteDeleteResolver())
            .build())
        .addTypeMapping(DatabaseLanguage.class, SQLiteTypeMapping.<DatabaseLanguage>builder()
            .putResolver(new DatabaseLanguageStorIOSQLitePutResolver())
            .getResolver(new DatabaseLanguageStorIOSQLiteGetResolver())
            .deleteResolver(new DatabaseLanguageStorIOSQLiteDeleteResolver())
            .build())
        .build();
  }

}
