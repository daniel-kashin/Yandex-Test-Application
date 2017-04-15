package com.danielkashin.yandextestapplication.data_layer.entities.translate.local;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.danielkashin.yandextestapplication.data_layer.contracts.translate.local.TranslationContract;
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
  Long language;

  @StorIOSQLiteColumn(name = TranslationContract.COLUMN_NAME_IS_FAVOURITE)
  Integer favorite;


  DatabaseTranslation() {
  }


  public DatabaseTranslation(@Nullable Long id, @NonNull String originalText, @NonNull String translatedText,
                             @NonNull  Long language, @NonNull Integer isFavourite) {
    this.id = id;
    this.originalText = originalText;
    this.translatedText = translatedText;
    this.language = language;
    this.favorite = isFavourite;
  }

  public Long getId() {
    return id;
  }

  public String getOriginalText() {
    return originalText;
  }

  public String getTranslatedText() {
    return translatedText;
  }

  public Integer isFavorite() {
    return favorite;
  }

  public Long getLanguage() {
    return language;
  }

}
