package com.danielkashin.yandextestapplication.presentation_layer.contracts;

/*
* values for connection between activities/fragments that are involved in picking languages
*/
public class PickLanguageContract {

  public final static String KEY_PICKED_TRANSLATION = "KEY_PICKED_TRANSLATION";

  public final static String KEY_PICK_LANGUAGE_TYPE = "KEY_PICK_LANGUAGE_TYPE";

  // just random int value
  public final static int REQUEST_TYPE_PICK_LANGUAGE = 142;


  public enum PickLanguageType {

    // just another random values
    PICK_ORIGINAL_LANGUAGE(389),

    PICK_TRANSLATED_LANGUAGE(567);


    private final int code;

    PickLanguageType(int code) {
      this.code = code;
    }

    public int getCode() {
      return code;
    }
  }
}
