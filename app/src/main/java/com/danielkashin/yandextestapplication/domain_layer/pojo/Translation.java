package com.danielkashin.yandextestapplication.domain_layer.pojo;


import android.os.Parcel;
import android.os.Parcelable;

public class Translation implements Parcelable {

  private String originalText;

  private String translatedText;

  private String languageCodePair;

  private Boolean favorite;


  public Translation(String originalText, String translatedText, String languageCodePair, Boolean favorite) {
    this.originalText = originalText;
    this.translatedText = translatedText;
    this.languageCodePair = languageCodePair;
    this.favorite = favorite;
  }

  public Translation(Parcel source){
    this.originalText = source.readString();
    this.translatedText = source.readString();
    this.languageCodePair = source.readString();
    this.favorite = source.readByte() == 1;
  }


  public String getTranslatedText(){
    return translatedText;
  }

  public String getOriginalText(){
    return originalText;
  }

  public String getLanguageCodePair(){
    return languageCodePair;
  }

  public Boolean ifFavorite(){
    return favorite;
  }

  public String[] getLanguageCodes(){
    return languageCodePair.split("-");
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel parcel, int i) {
    parcel.writeString(originalText);
    parcel.writeString(translatedText);
    parcel.writeString(languageCodePair);
    parcel.writeByte((byte)(favorite ? 1 : 0));
  }

  public static final Parcelable.Creator<Translation> CREATOR = new Parcelable.Creator<Translation>(){
    @Override
    public Translation createFromParcel(Parcel parcel) {
      return new Translation(parcel);
    }

    @Override
    public Translation[] newArray(int i) {
      return new Translation[i];
    }
  };

}
