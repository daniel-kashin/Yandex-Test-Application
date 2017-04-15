package com.danielkashin.yandextestapplication.domain_layer.pojo;

import com.danielkashin.yandextestapplication.data_layer.entities.supported_languages.local.Language;


public class LanguagePair {

  private Language originalLanguage;
  private Language translatedLanguage;


  public LanguagePair(){
  }

  public LanguagePair(Language originalLanguage, Language translatedLanguage) {
    this.originalLanguage = originalLanguage;
    this.translatedLanguage = translatedLanguage;
  }


  public void setOriginalLanguage(Language originalLanguage) {
    this.originalLanguage = originalLanguage;
  }

  public void setTranslatedLanguage(Language originalLanguage) {
    this.translatedLanguage = translatedLanguage;
  }

  public Language getOriginalLanguage() {
    return originalLanguage;
  }

  public Language getTranslatedLanguage() {
    return translatedLanguage;
  }

  public String getLanguageCodePair(){
    if (originalLanguage == null || translatedLanguage == null){
      throw new IllegalStateException("Both languages must be non null");
    }

    return originalLanguage.getCode() + "-" + translatedLanguage.getCode();
  }

  public void swapLanguages(){
    Language buffer = originalLanguage;
    originalLanguage = translatedLanguage;
    translatedLanguage = buffer;
  }
}
