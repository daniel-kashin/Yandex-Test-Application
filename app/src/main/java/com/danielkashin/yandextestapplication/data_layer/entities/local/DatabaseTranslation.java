package com.danielkashin.yandextestapplication.data_layer.entities.local;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.danielkashin.yandextestapplication.data_layer.database.TranslationContract;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;


@StorIOSQLiteType(table = TranslationContract.TABLE_NAME)
public class DatabaseTranslation {

  @StorIOSQLiteColumn(name = TranslationContract.COLUMN_NAME_ID, key = true)
  Long id;

  @StorIOSQLiteColumn(name = TranslationContract.COLUMN_NAME_ORIGINAL_TEXT)
  String originalText;

  @StorIOSQLiteColumn(name = TranslationContract.COLUMN_NAME_TRANSLATED_TEXT)
  String translatedText;

  @StorIOSQLiteColumn(name = TranslationContract.COLUMN_NAME_LANGUAGE)
  String language;

  @StorIOSQLiteColumn(name = TranslationContract.COLUMN_NAME_IS_FAVOURITE)
  Integer isFavourite;


  DatabaseTranslation() {
  }


  public DatabaseTranslation(@Nullable Long id, @NonNull String originalText, @NonNull String translatedText,
                             @NonNull  String language, @NonNull Integer isFavourite) {

    this.id = id;
    this.originalText = originalText;
    this.translatedText = translatedText;
    this.language = language;
    this.isFavourite = isFavourite;
  }

}
