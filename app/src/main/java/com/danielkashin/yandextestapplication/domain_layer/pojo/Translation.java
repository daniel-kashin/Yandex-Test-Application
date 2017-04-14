package com.danielkashin.yandextestapplication.domain_layer.pojo;


import android.os.Parcel;
import android.os.Parcelable;

public class Translation implements Parcelable {

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


  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel parcel, int i) {
    parcel.writeString(originalText);
    parcel.writeString(translatedText);
    parcel.writeString(language);
    parcel.writeByte((byte)(favorite ? 0 : 1));
  }

}
