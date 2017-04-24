package com.danielkashin.yandextestapplication.data_layer.entities.translate.local;

import com.danielkashin.yandextestapplication.data_layer.contracts.translate.local.LanguageContract;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

/*
 * languages are stored in their own table and are accessed by the index
 * not to duplicate data
 * language and its code from SQLite Database. StorIO is a great library-adapter
 */
@StorIOSQLiteType(table = LanguageContract.TABLE_NAME)
public class DatabaseLanguage {

  // package-private accessibility modifier is required for the code-generation
  @StorIOSQLiteColumn(name = LanguageContract.COLUMN_NAME_ID, key = true)
  Long id;

  @StorIOSQLiteColumn(name = LanguageContract.COLUMN_NAME_LANGUAGE)
  String language;


  // is required for the code-generation
  DatabaseLanguage() {
  }

  public DatabaseLanguage(Long id, String language) {
    this.id = id;
    this.language = language;
  }


  public Long getId() {
    return id;
  }

  public String getLanguage() {
    return language;
  }

}
