package com.danielkashin.yandextestapplication.domain_layer.pojo;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.danielkashin.yandextestapplication.data_layer.entities.supported_languages.local.Language;


public class LanguagePair {

  private static final String KEY_ORIGINAL_LANGUAGE = "KEY_ORIGINAL_LANGUAGE";
  private static final String KEY_TRANSLATED_LANGUAGE = "KEY_TRANSLATED_LANGUAGE";

  private Language originalLanguage;
  private Language translatedLanguage;


  public LanguagePair(Bundle bundle) throws IllegalStateException {
    if (bundle == null || !bundle.containsKey(KEY_ORIGINAL_LANGUAGE)
        || !bundle.containsKey(KEY_TRANSLATED_LANGUAGE)
        || bundle.getParcelable(KEY_ORIGINAL_LANGUAGE) == null
        || bundle.getParcelable(KEY_TRANSLATED_LANGUAGE) == null){
      throw new IllegalStateException("Bundle must contain languages");
    }

    this.originalLanguage = bundle.getParcelable(KEY_ORIGINAL_LANGUAGE);
    this.translatedLanguage = bundle.getParcelable(KEY_TRANSLATED_LANGUAGE);
  }

  public LanguagePair(Language originalLanguage, Language translatedLanguage) {
    if (originalLanguage == null || translatedLanguage == null){
      throw new IllegalStateException("Both languages bust be non null");
    }

    this.originalLanguage = originalLanguage;
    this.translatedLanguage = translatedLanguage;
  }


  public void setOriginalLanguage(Language originalLanguage) {
    if (originalLanguage == null){
      throw new IllegalStateException("Language must be non null");
    }

    this.originalLanguage = originalLanguage;
  }

  public void setTranslatedLanguage(Language translatedLanguage) {
    if (translatedLanguage == null){
      throw new IllegalStateException("Language must be non null");
    }

    this.translatedLanguage = translatedLanguage;
  }

  public Language getOriginalLanguage() {
    return originalLanguage;
  }

  public Language getTranslatedLanguage() {
    return translatedLanguage;
  }

  public String getLanguageCodePair(){
    return originalLanguage.getCode() + "-" + translatedLanguage.getCode();
  }

  public void saveToBundle(Bundle bundle){
    bundle.putParcelable(KEY_ORIGINAL_LANGUAGE, originalLanguage);
    bundle.putParcelable(KEY_TRANSLATED_LANGUAGE, translatedLanguage);
  }

  public void swapLanguages(){
    Language buffer = originalLanguage;
    originalLanguage = translatedLanguage;
    translatedLanguage = buffer;
  }
}
