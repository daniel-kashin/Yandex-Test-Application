package com.danielkashin.yandextestapplication.domain_layer.pojo;

import android.os.Bundle;

/*
* common pair of two languages: original and translated
*/
public class LanguagePair {

  // keys to save instance into bundle
  private static final String KEY_ORIGINAL_LANGUAGE = "KEY_ORIGINAL_LANGUAGE";
  private static final String KEY_TRANSLATED_LANGUAGE = "KEY_TRANSLATED_LANGUAGE";

  private Language originalLanguage;
  private Language translatedLanguage;


  public LanguagePair(Language originalLanguage, Language translatedLanguage) {
    if (originalLanguage == null || translatedLanguage == null) {
      throw new IllegalArgumentException("All languages in LanguagePair constructor must be non null");
    }

    this.originalLanguage = originalLanguage;
    this.translatedLanguage = translatedLanguage;
  }

  // ---------------------------------------- public ----------------------------------------------

  public void setOriginalLanguage(Language originalLanguage) {
    if (originalLanguage == null) {
      throw new IllegalArgumentException("Language in setter must be non null");
    }

    this.originalLanguage = originalLanguage;
  }

  public void setTranslatedLanguage(Language translatedLanguage) {
    if (translatedLanguage == null) {
      throw new IllegalArgumentException("Language in setter must be non null");
    }

    this.translatedLanguage = translatedLanguage;
  }

  public Language getOriginalLanguage() {
    return originalLanguage;
  }

  public Language getTranslatedLanguage() {
    return translatedLanguage;
  }

  public String getLanguageCodePair() {
    return originalLanguage.getCode() + "-" + translatedLanguage.getCode();
  }

  public void saveToBundle(Bundle bundle) {
    bundle.putParcelable(KEY_ORIGINAL_LANGUAGE, originalLanguage);
    bundle.putParcelable(KEY_TRANSLATED_LANGUAGE, translatedLanguage);
  }

  public void swapLanguages() {
    Language buffer = originalLanguage;
    originalLanguage = translatedLanguage;
    translatedLanguage = buffer;
  }

  // ------------------------------------- inner types --------------------------------------------

  public static class Factory {

    private Factory() {
    }

    public static LanguagePair create(Bundle bundle) throws IllegalArgumentException {
      if (bundle == null || !bundle.containsKey(KEY_ORIGINAL_LANGUAGE)
          || !bundle.containsKey(KEY_TRANSLATED_LANGUAGE)) {
        throw new IllegalArgumentException("Bundle must contain languages");
      }

      Language originalLanguage = bundle.getParcelable(KEY_ORIGINAL_LANGUAGE);
      Language translatedLanguage = bundle.getParcelable(KEY_TRANSLATED_LANGUAGE);

      if (originalLanguage == null || translatedLanguage == null) {
        throw new IllegalArgumentException("Languages in bundle must be non null");
      }

      return new LanguagePair(originalLanguage, translatedLanguage);
    }
  }
}
