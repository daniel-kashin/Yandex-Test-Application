package com.danielkashin.yandextestapplication.data_layer.entities.supported_languages.local;


public class DatabaseSupportedLanguage {

  private final String code;
  private final String text;


  public DatabaseSupportedLanguage(String code, String text) {
    this.code = code;
    this.text = text;
  }

  public String getCode() {
    return code;
  }

  public String getText() {
    return text;
  }

}
