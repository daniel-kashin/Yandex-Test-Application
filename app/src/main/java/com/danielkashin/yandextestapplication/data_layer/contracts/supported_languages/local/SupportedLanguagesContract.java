package com.danielkashin.yandextestapplication.data_layer.contracts.supported_languages.local;

import java.util.HashMap;

/*
 * checking for new languages through the API doesn`t worth it for many reasons
 */
public class SupportedLanguagesContract {

  public static final String DEFAULT_ORIGINAL_LANGUAGE_CODE = "ru";
  public static final String DEFAULT_TRANSLATED_LANGUAGE_CODE = "en";

  public static String[] SUPPORTED_LANGUAGES_CODES = new String[]{
      "af", "am", "ar", "az", "ba", "be", "bg", "bn", "bs", "ca",
      "ceb", "cs", "cy", "da", "de", "el", "en", "eo", "es", "et",
      "eu", "fa", "fi", "fr", "ga", "gd", "gl", "gu", "he", "hi",
      "hr", "ht", "hu", "hy", "id", "is", "it", "ja", "jv", "ka",
      "kk", "km", "kn", "ko", "ky", "la", "lb", "lo", "lt", "lv",
      "mg", "mhr", "mi", "mk", "ml", "mn", "mr", "mrj", "ms", "mt",
      "my", "ne", "nl", "no", "pa", "pap", "pl", "pt", "ro", "ru",
      "si", "sk", "sl", "sq", "sr", "su", "sv", "sw", "ta", "te",
      "tg", "th", "tl", "tr", "tt", "udm", "uk", "ur", "uz", "vi",
      "xh", "yi", "zh"
  };

  public static HashMap<String, Integer> getMap() {
    // get index by text for the constant time
    HashMap<String, Integer> hashMap = new HashMap<>();
    for (int i = 0; i < SUPPORTED_LANGUAGES_CODES.length; ++i) {
      hashMap.put(SUPPORTED_LANGUAGES_CODES[i], i);
    }
    return hashMap;
  }
}
