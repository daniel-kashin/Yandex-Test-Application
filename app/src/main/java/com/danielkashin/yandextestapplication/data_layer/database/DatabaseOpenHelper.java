package com.danielkashin.yandextestapplication.data_layer.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.danielkashin.yandextestapplication.BuildConfig;
import com.danielkashin.yandextestapplication.data_layer.contracts.translate.local.LanguageContract;
import com.danielkashin.yandextestapplication.data_layer.contracts.translate.local.TranslationContract;

/*
 * I tried to use Realm, but it requires to change the whole architecture and do not allow
 * to treat objects from Realm and API the one way, completely destroying Clean Architecture
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {

  public DatabaseOpenHelper(Context context) {
    super(context, BuildConfig.DATABASE_NAME, null, BuildConfig.DATABASE_BUILD_NUMBER);
  }

  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {
    sqLiteDatabase.execSQL(TranslationContract.SQL_CREATE_TABLE);
    sqLiteDatabase.execSQL(LanguageContract.SQL_CREATE_TABLE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    sqLiteDatabase.execSQL(TranslationContract.SQL_DELETE_TABLE);
    sqLiteDatabase.execSQL(LanguageContract.SQL_DELETE_TABLE);
    onCreate(sqLiteDatabase);
  }
}
