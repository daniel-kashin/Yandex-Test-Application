package com.danielkashin.yandextestapplication.data_layer.contracts.translate.local;


public class TranslationContract {

  private TranslationContract() {}


  // -------------------------------------- constants ---------------------------------------------

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

  // ----------------------------------- static methods ------------------------------------------

  public static String getTranslationSearchQuery(boolean onlyFavorite, String searchRequest) {
    StringBuilder searchBuilder = new StringBuilder("");

    if (onlyFavorite) {
      searchBuilder.append(TranslationContract.COLUMN_NAME_IS_FAVOURITE)
          .append(" = ")
          .append(1);
    }

    if (searchRequest != null && !searchRequest.isEmpty()) {
      searchBuilder.append(onlyFavorite ? "\nAND" : "")
          .append("(")
          .append(TranslationContract.COLUMN_NAME_ORIGINAL_TEXT)
          .append(" LIKE \'%")
          .append(searchRequest)
          .append("%\'")
          .append(onlyFavorite ? "" : ")")
          .append(" OR ")
          .append(onlyFavorite ? "" : "(")
          .append(TranslationContract.COLUMN_NAME_TRANSLATED_TEXT)
          .append(" LIKE \'%")
          .append(searchRequest)
          .append("%\'")
          .append(")");
    }

    String result = searchBuilder.toString();
    if (result.equals("")) {
      return null;
    } else {
      return result;
    }
  }
}
