package com.danielkashin.yandextestapplication.data_layer.contracts.translate.local;


public class TranslationContract {

  private TranslationContract() {
  }


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
      + COLUMN_NAME_IS_FAVOURITE + " TEXT NOT NULL, "
      + "UNIQUE(" + COLUMN_NAME_ORIGINAL_TEXT + ", " + COLUMN_NAME_LANGUAGE + ") ON CONFLICT REPLACE"
      + ");";

  public static String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

  private static final String ESCAPE_CHAR = "#";

  // --------------------------------------- public -----------------------------------------------

  public static String getGetTranslationSearchQuery(String originalText, int languageCode) {
    String result =  new StringBuilder().append(COLUMN_NAME_ORIGINAL_TEXT)
        .append(" = \'")
        .append(getEscapedString(originalText))
        .append("\'\n")
        .append("AND ")
        .append(COLUMN_NAME_LANGUAGE)
        .append(" = ")
        .append(languageCode)
        .toString();

    return result;
  }



  public static String getGetTranslationsSearchQuery(boolean onlyFavorite, String searchRequest) {
    StringBuilder searchBuilder = new StringBuilder();

    // add query to get only favorite translations
    if (onlyFavorite) {
      searchBuilder.append(TranslationContract.COLUMN_NAME_IS_FAVOURITE)
          .append(" = ")
          .append(1);
    }

    if (searchRequest != null && !searchRequest.isEmpty()) {
      searchBuilder

          // if we added query to find only favorite, then we must add AND keyword
          .append(onlyFavorite ? "\nAND" : "")

          // translations where original text contains search request will match
          .append("(")
          .append(TranslationContract.COLUMN_NAME_ORIGINAL_TEXT)
          .append(" LIKE \'%")
          .append(getEscapedString(searchRequest))
          .append("%\'")
          .append(" ESCAPE ")
          .append("\'")
          .append(ESCAPE_CHAR)
          .append("\'")
          .append(onlyFavorite ? "" : ")")

          // translations where translated text contains search request will also match
          .append(" OR ")
          .append(onlyFavorite ? "" : "(")
          .append(TranslationContract.COLUMN_NAME_TRANSLATED_TEXT)
          .append(" LIKE \'%")
          .append(getEscapedString(searchRequest))
          .append("%\'")
          .append(" ESCAPE ")
          .append("\'")
          .append(ESCAPE_CHAR)
          .append("\'")
          .append(")");
    }

    return searchBuilder.toString();
  }

  public static String getEscapedString(String string) {
    return string.replace(ESCAPE_CHAR, ESCAPE_CHAR + ESCAPE_CHAR)
        .replace("'", "''")
        .replace("%", ESCAPE_CHAR + "%")
        .replace("_", ESCAPE_CHAR + "_");
  }
}
