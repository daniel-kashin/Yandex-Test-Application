package com.danielkashin.yandextestapplication.data_layer.database;


public class TranslationContract {

  private TranslationContract() {}


  public static final String TABLE_NAME = "table_name_translations";

  public static final String COLUMN_NAME_ID = "_id";

  public static final String COLUMN_NAME_ORIGINAL_TEXT = "original_text";

  public static final String COLUMN_NAME_TRANSLATED_TEXT = "translated_text";

  public static final String COLUMN_NAME_LANGUAGE = "language";

  public static final String COLUMN_NAME_IS_FAVOURITE = "is_favourite";

  public static String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
      + COLUMN_NAME_ID + " INTEGER NOT NULL PRIMARY KEY, "
      + COLUMN_NAME_ORIGINAL_TEXT + " TEXT NOT NULL, "
      + COLUMN_NAME_TRANSLATED_TEXT + " TEXT NOT NULL, "
      + COLUMN_NAME_LANGUAGE + " INTEGER NOT NULL, "
      + COLUMN_NAME_IS_FAVOURITE + " TEXT NOT NULL"
      + ");";

  public static String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}
