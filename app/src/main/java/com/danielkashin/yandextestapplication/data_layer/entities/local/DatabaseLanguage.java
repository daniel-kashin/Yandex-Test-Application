package com.danielkashin.yandextestapplication.data_layer.entities.local;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.danielkashin.yandextestapplication.data_layer.database.LanguageContract;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;


@StorIOSQLiteType(table = LanguageContract.TABLE_NAME)
public class DatabaseLanguage {

  @StorIOSQLiteColumn(name = LanguageContract.COLUMN_NAME_ID, key = true)
  Long id;

  @StorIOSQLiteColumn(name = LanguageContract.COLUMN_NAME_LANGUAGE)
  String language;


  DatabaseLanguage(){
  }


  public DatabaseLanguage(@Nullable Long id, @NonNull String language){
    this.id = id;
    this.language = language;
  }

  public Long getId() {
    return id;
  }

  public String getLanguage(){
    return language;
  }

}
