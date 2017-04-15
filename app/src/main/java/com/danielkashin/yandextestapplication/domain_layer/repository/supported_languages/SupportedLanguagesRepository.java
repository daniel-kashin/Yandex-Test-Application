package com.danielkashin.yandextestapplication.domain_layer.repository.supported_languages;

import com.danielkashin.yandextestapplication.data_layer.entities.supported_languages.local.Language;
import com.danielkashin.yandextestapplication.data_layer.services.supported_languages.local.ISupportedLanguagesLocalService;
import com.danielkashin.yandextestapplication.data_layer.services.supported_languages.local.SupportedLanguagesLocalService;
import com.danielkashin.yandextestapplication.domain_layer.pojo.LanguagePair;
import java.util.ArrayList;


public class SupportedLanguagesRepository implements ISupportedLanguagesRepository {

  private final ISupportedLanguagesLocalService localService;


  public SupportedLanguagesRepository(ISupportedLanguagesLocalService localService){
    this.localService = localService;
  }

  @Override
  public LanguagePair getDefaultLanguages() {
    Language originalLanguage = localService.getDefaultOriginalLanguage();
    Language translatedLanguage = localService.getDefaultTranslatedLanguage();
    return new LanguagePair(originalLanguage, translatedLanguage);
  }

  @Override
  public LanguagePair getLanguages(String originalCode, String translatedCode) {
    Language originalLanguage = localService.getLanguage(originalCode);
    Language translatedLanguage = localService.getLanguage(translatedCode);
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
