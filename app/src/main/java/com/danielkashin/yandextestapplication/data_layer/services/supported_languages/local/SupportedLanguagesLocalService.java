package com.danielkashin.yandextestapplication.data_layer.services.supported_languages.local;

import android.content.Context;

import com.danielkashin.yandextestapplication.R;
import com.danielkashin.yandextestapplication.data_layer.contracts.supported_languages.local.SupportedLanguagesContract;
import com.danielkashin.yandextestapplication.data_layer.entities.supported_languages.local.Language;

import java.util.ArrayList;
import java.util.HashMap;


public class SupportedLanguagesLocalService implements ISupportedLanguagesLocalService {

  private final Context context;
  private final HashMap<String, Integer> codes;


  private SupportedLanguagesLocalService(Context context) {
    this.context = context.getApplicationContext();

    checkLengths();

    codes = SupportedLanguagesContract.getMap();
  }


  @Override
  public Language getDefaultOriginalLanguage() {
    return getLanguage(SupportedLanguagesContract.DEFAULT_ORIGINAL_LANGUAGE_CODE);
  }

  @Override
  public Language getDefaultTranslatedLanguage() {
    return getLanguage(SupportedLanguagesContract.DEFAULT_TRANSLATED_LANGUAGE_CODE);
  }

  @Override
  public Language getLanguage(String code) {
    checkLengths();
    checkCode(code);

    String languageText = context.getResources()
        .getStringArray(R.array.supported_languages_texts)
        [codes.get(code)];
    return new Language(code, languageText);
  }

  @Override
  public ArrayList<Language> getAllLanguages() {
    checkLengths();

    String[] languageTexts = context.getResources()
        .getStringArray(R.array.supported_languages_texts);
    String[] languageCodes = SupportedLanguagesContract.SUPPORTED_LANGUAGES_CODES;

    ArrayList<Language> languages = new ArrayList<>();
    for (int i = 0; i < languageCodes.length; ++i) {
      languages.add(new Language(languageCodes[i], languageTexts[i]));
    }

    return languages;
  }


  private void checkLengths() {
    if (context.getResources().getStringArray(R.array.supported_languages_texts).length !=
        SupportedLanguagesContract.SUPPORTED_LANGUAGES_CODES.length) {
      throw new IllegalStateException("Lengths of supported languages should equal");
    }
  }

  private void checkCode(String code) {
    if (!codes.containsKey(code)) {
      throw new IllegalStateException("Code must be supported by the contract");
    }
  }


  public static class Factory {

    private Factory() {
    }

    public static ISupportedLanguagesLocalService create(Context context){
      return new SupportedLanguagesLocalService(context);
    }

  }


}
