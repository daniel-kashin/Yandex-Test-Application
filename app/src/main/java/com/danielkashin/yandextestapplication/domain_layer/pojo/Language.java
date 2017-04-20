package com.danielkashin.yandextestapplication.domain_layer.pojo;

import android.os.Parcel;
import android.os.Parcelable;


public class Language implements Parcelable {

  private final String code;
  private final String text;


  public Language(String code, String text) {
    this.code = code;
    this.text = text;
  }

  private Language(Parcel parcel){
    this.code = parcel.readString();
    this.text = parcel.readString();
  }

  public String getCode() {
    return code;
  }

  public String getText() {
    return text;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(code);
    dest.writeString(text);
  }


  public static final Parcelable.Creator<Language> CREATOR = new Parcelable.Creator<Language>() {
    @Override
    public Language createFromParcel(Parcel parcel) {
      return new Language(parcel);
    }

    @Override
    public Language[] newArray(int size) {
      return new Language[size];
    }
  };

}
