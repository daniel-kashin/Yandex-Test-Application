package com.danielkashin.yandextestapplication.domain_layer.repository.supported_languages;

import android.support.annotation.NonNull;

import com.danielkashin.yandextestapplication.data_layer.entities.supported_languages.local.DatabaseLanguage;
import com.danielkashin.yandextestapplication.data_layer.services.supported_languages.local.ISupportedLanguagesLocalService;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Language;
import com.danielkashin.yandextestapplication.domain_layer.pojo.LanguagePair;
import java.util.ArrayList;


public class SupportedLanguagesRepository implements ISupportedLanguagesRepository {

  private final ISupportedLanguagesLocalService localService;


  public SupportedLanguagesRepository(ISupportedLanguagesLocalService localService){
    if (localService == null) {
      throw new IllegalArgumentException("All arguments must be non null");
    }

    this.localService = localService;
  }

  @NonNull
  @Override
  public LanguagePair getDefaultLanguages() {
    DatabaseLanguage original = localService.getDefaultOriginalLanguage();
    DatabaseLanguage translated = localService.getDefaultTranslatedLanguage();

    Language originalLanguage = new Language(original.getCode(), original.getText());
    Language translatedLanguage = new Language(translated.getCode(), translated.getText());

    return new LanguagePair(originalLanguage, translatedLanguage);
  }

  @NonNull
  @Override
  public LanguagePair getLanguages(String originalCode, String translatedCode) {
    DatabaseLanguage original = localService.getLanguage(originalCode);
    DatabaseLanguage translated = localService.getLanguage(translatedCode);

    Language originalLanguage = new Language(original.getCode(), original.getText());
    Language translatedLanguage = new Language(translated.getCode(), translated.getText());

    return new LanguagePair(originalLanguage, translatedLanguage);
  }

  @Override
  public Language getLanguage(String code) {
    return null;
  }

  @Override
  public ArrayList<Language> getAllLanguages() {
    return null;
  }


  public static class Factory {

    private Factory(){
    }

    public static ISupportedLanguagesRepository create(ISupportedLanguagesLocalService service){
      return new SupportedLanguagesRepository(service);
    }

  }
}
