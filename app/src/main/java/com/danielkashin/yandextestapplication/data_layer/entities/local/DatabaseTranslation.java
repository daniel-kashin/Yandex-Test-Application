package com.danielkashin.yandextestapplication.data_layer.entities.local;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;


public class DatabaseTranslation extends RealmObject {

  @Required
  private String originalText;

  @Required
  private String translatedText;

  @Required
  private String language;

  @Required
  private Boolean isFavourite;


  public Boolean getIsFavourite(){
    return isFavourite;
  }

  public String getOriginalText() {
    return originalText;
  }

  public String getTranslatedText() {
    return translatedText;
  }

  public void setOriginalText(String originalText){
    this.originalText = originalText;
  }

  public void setTranslatedText(String translatedText){
    this.translatedText = translatedText;
  }

  public void setLanguage(String language){
    this.language = language;
  }

  public void setIsFavourite(boolean isFavourite){
    this.isFavourite = isFavourite;
  }

}
