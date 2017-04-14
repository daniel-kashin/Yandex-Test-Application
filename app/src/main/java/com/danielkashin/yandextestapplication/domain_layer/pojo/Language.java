package com.danielkashin.yandextestapplication.domain_layer.pojo;


public class Language {

  private final String originalLanguage;

  private final String translatedLanguage;


  public Language(String originalLanguage, String translatedLanguage){
    this.originalLanguage = originalLanguage;
    this.translatedLanguage = translatedLanguage;
  }


  public String getOriginalLanguage() {
    return originalLanguage;
  }

  public String getTranslatedLanguage() {
    return translatedLanguage;
  }
}
