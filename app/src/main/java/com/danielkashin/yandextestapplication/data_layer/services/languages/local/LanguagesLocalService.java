package com.danielkashin.yandextestapplication.data_layer.services.languages.local;

import android.content.Context;

import com.danielkashin.yandextestapplication.R;
import com.danielkashin.yandextestapplication.data_layer.contracts.supported_languages.local.SupportedLanguagesContract;
import com.danielkashin.yandextestapplication.data_layer.entities.supported_languages.local.DatabaseLanguage;

import java.util.ArrayList;
import java.util.HashMap;


public class LanguagesLocalService implements ILanguagesLocalService {

  private final Context context;
  private final HashMap<String, Integer> codes;


  private LanguagesLocalService(Context context) {
    this.context = context.getApplicationContext();

    checkLengths();

    codes = SupportedLanguagesContract.getMap();
  }


  @Override
  public DatabaseLanguage getDefaultOriginalLanguage() {
    return getLanguage(SupportedLanguagesContract.DEFAULT_ORIGINAL_LANGUAGE_CODE);
  }

  @Override
  public DatabaseLanguage getDefaultTranslatedLanguage() {
    return getLanguage(SupportedLanguagesContract.DEFAULT_TRANSLATED_LANGUAGE_CODE);
  }

  @Override
  public DatabaseLanguage getLanguage(String code) {
    checkLengths();
    checkCode(code);

    String languageText = context.getResources()
        .getStringArray(R.array.supported_languages_texts)
        [codes.get(code)];
    return new DatabaseLanguage(code, languageText);
  }

  @Override
  public ArrayList<DatabaseLanguage> getAllLanguages() {
    checkLengths();

    String[] languageTexts = context.getResources()
        .getStringArray(R.array.supported_languages_texts);
    String[] languageCodes = SupportedLanguagesContract.SUPPORTED_LANGUAGES_CODES;

    ArrayList<DatabaseLanguage> languages = new ArrayList<>();
    for (int i = 0; i < languageCodes.length; ++i) {
      languages.add(new DatabaseLanguage(languageCodes[i], languageTexts[i]));
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

    public static ILanguagesLocalService create(Context context){
      return new LanguagesLocalService(context);
    }

  }


}
