package com.danielkashin.yandextestapplication.domain_layer.pojo;


public class Translation {

  private String originalText;

  private String translatedText;

  private String language;

  private Boolean favorite;


  public Translation(String originalText, String translatedText, String language, Boolean favorite) {
    this.originalText = originalText;
    this.translatedText = translatedText;
    this.language = language;
    this.favorite = favorite;
  }


  public String getTranslatedText(){
    return translatedText;
  }

  public String getOriginalText(){
    return originalText;
  }

  public String getLanguage(){
    return language;
  }

  public Boolean ifFavorite(){
    return favorite;
  }

}
