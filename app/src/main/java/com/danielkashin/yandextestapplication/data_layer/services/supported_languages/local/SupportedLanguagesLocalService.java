package com.danielkashin.yandextestapplication.data_layer.services.supported_languages.local;

import android.content.Context;

import com.danielkashin.yandextestapplication.R;
import com.danielkashin.yandextestapplication.data_layer.contracts.supported_languages.local.SupportedLanguagesContract;
import com.danielkashin.yandextestapplication.data_layer.entities.supported_languages.local.DatabaseSupportedLanguage;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


public class SupportedLanguagesLocalService implements ISupportedLanguagesLocalService {

  private final Context context;
  // get index of the language from its text by the constant time
  private final HashMap<String, Integer> codes;


  private SupportedLanguagesLocalService(Context context) {
    this.context = context.getApplicationContext();

    checkLengths();

    codes = SupportedLanguagesContract.getMap();
  }


  @Override
  public DatabaseSupportedLanguage getDefaultOriginalLanguage() {
    return getLanguage(SupportedLanguagesContract.DEFAULT_ORIGINAL_LANGUAGE_CODE);
  }

  @Override
  public DatabaseSupportedLanguage getDefaultTranslatedLanguage() {
    return getLanguage(SupportedLanguagesContract.DEFAULT_TRANSLATED_LANGUAGE_CODE);
  }

  @Override
  public DatabaseSupportedLanguage getLanguage(String code) {
    checkLengths();
    checkCode(code);

    // storing language texts in resources allows us to easily localize them to the languages we want
    String languageText = context.getResources()
        .getStringArray(R.array.supported_languages_texts)
        [codes.get(code)];
    return new DatabaseSupportedLanguage(code, languageText);
  }

  @Override
  public ArrayList<DatabaseSupportedLanguage> getAllLanguages() {
    checkLengths();

    String[] languageTexts = context.getResources()
        .getStringArray(R.array.supported_languages_texts);
    String[] languageCodes = SupportedLanguagesContract.SUPPORTED_LANGUAGES_CODES;

    ArrayList<DatabaseSupportedLanguage> languages = new ArrayList<>();
    for (int i = 0; i < languageCodes.length; ++i) {
      languages.add(new DatabaseSupportedLanguage(languageCodes[i], languageTexts[i]));
    }

    // we must do that as user`s current languages can change
    Collections.sort(languages, new Comparator<DatabaseSupportedLanguage>() {
      @Override
      public int compare(DatabaseSupportedLanguage o1, DatabaseSupportedLanguage o2) {
        return Collator.getInstance().compare(o1.getText(), o2.getText());
      }
    });

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
