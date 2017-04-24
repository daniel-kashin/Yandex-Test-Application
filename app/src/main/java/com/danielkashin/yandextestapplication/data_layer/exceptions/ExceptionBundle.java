package com.danielkashin.yandextestapplication.data_layer.exceptions;

import android.os.Bundle;

/*
 * in clean architecture something like that can be used for elegant exception-handling.
 * allows to get exception type and some additional information
 */
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

  public void addStringExtra(String key, String value) {
    this.extras.putString(key, value);
  }

  public void addIntExtra(String key, int extra) {
    this.extras.putInt(key, extra);
  }

  public String getStringExtra(String key) {
    return this.extras.getString(key);
  }

  public int getIntExtra(String key) {
    return this.extras.getInt(key);
  }


  // reason of the exception. codes in future can be used
  // for getting the source of exception: api, database
  public enum Reason {

    // general exceptions
    UNKNOWN(-001),
    NETWORK_UNAVAILABLE(000),
    SERVICE_UNSUPPORTED(001),

    // api exceptions
    WRONG_KEY(401),
    LIMIT_EXPIRED(404),
    TEXT_LIMIT_EXPIRED(413),
    WRONG_TEXT(422),
    WRONG_LANGS(501),

    // database exceptions
    EMPTY_TRANSLATION(600),
    EMPTY_LANGUAGE(601),
    EMPTY_TRANSLATIONS(602),
    PUT_DENIED(603),
    TRANSLATION_FAVORITE_EQUALS_CHANGED(604),
    NULL_POINTER(605),
    DELETE_DENIED(606),
    DELETE_SOURCE_IS_EMPTY(607),
    DATABASE_CLOSED(608);


    private final int code;

    Reason(int code){
      this.code = code;
    }

  }
}
