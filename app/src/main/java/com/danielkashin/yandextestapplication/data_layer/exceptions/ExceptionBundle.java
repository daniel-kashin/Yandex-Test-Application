package com.danielkashin.yandextestapplication.data_layer.exceptions;


import android.os.Bundle;

public class ExceptionBundle extends Exception {

  private ExceptionBundle.Reason reason;
  private Bundle extras;


  public ExceptionBundle(Reason reason) {
    this.reason = reason;
    this.extras = new Bundle();
  }

  public Reason getReason() {
    return reason;
  }

  public void addThrowable(String key, Throwable throwable) {
    this.extras.putSerializable(key, throwable);
  }

  public void addStringExtra(String key, String value) {
    this.extras.putString(key, value);
  }

  public void addIntExtra(String key, int extra) {
    this.extras.putInt(key, extra);
  }

  public Throwable getThrowable(String key) {
    return (Throwable) this.extras.getSerializable(key);
  }

  public String getStringExtra(String key) {
    return this.extras.getString(key);
  }

  public int getIntExtra(String key) {
    return this.extras.getInt(key);
  }


  public enum Reason {

    UNKNOWN(-001),
    NETWORK_UNAVAILABLE(000),

    WRONG_KEY(401),
    LIMIT_EXPIRED(404),
    TEXT_LIMIT_EXPIRED(413),
    WRONG_TEXT(422),
    WRONG_LANGS(501),

    EMPTY_TRANSLATION(600),
    EMPTY_LANGUAGE(601),
    EMPTY_TRANSLATIONS(602),
    PUT_DENIED(603),
    TRANSLATION_FAVORITE_EQUALS_CHANGED(604),
    NULL_FIELD(605),
    DATABASE_CLOSED(606),

    SERVICE_UNSUPPORTED(800);

    private final int code;

    Reason(int code){
      this.code = code;
    }

  }
}
