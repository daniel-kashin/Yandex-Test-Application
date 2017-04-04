package com.danielkashin.yandextestapplication.domain_layer.pojo;


public class Translation {

  private String originalText;

  private String translatedText;

  private String language;

  private Boolean isFavourite;


  public Translation(String originalText, String translatedText, String language, Boolean isFavourite) {
    this.originalText = originalText;
    this.translatedText = translatedText;
    this.language = language;
    this.isFavourite = isFavourite;
  }


  public String getTranslatedText(){
    return translatedText;
  }
}
