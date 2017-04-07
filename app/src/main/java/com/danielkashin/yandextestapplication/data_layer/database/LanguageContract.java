package com.danielkashin.yandextestapplication.data_layer.database;


public class LanguageContract {

  private LanguageContract() {}


  public static final String TABLE_NAME = "table_name_language";

  public static final String COLUMN_NAME_ID = "_id";

  public static final String COLUMN_NAME_LANGUAGE = "original_text";

  public static String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
      + COLUMN_NAME_ID + " INTEGER NOT NULL PRIMARY KEY, "
      + COLUMN_NAME_LANGUAGE + " TEXT UNIQUE NOT NULL"
      + ");";

  public static String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

}
