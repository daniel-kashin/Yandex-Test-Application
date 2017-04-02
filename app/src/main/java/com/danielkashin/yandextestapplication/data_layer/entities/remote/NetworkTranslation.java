package com.danielkashin.yandextestapplication.data_layer.entities.remote;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class NetworkTranslation {

  @SerializedName("code")
  @Expose
  private int code;

  @SerializedName("lang")
  @Expose
  private String language;

  @SerializedName("text")
  private ArrayList<String> text;

  public String getText() {
    return text.get(0);
  }

  public String getLanguage() {
    return language;
  }

}
