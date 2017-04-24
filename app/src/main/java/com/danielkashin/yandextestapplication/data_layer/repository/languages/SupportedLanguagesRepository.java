package com.danielkashin.yandextestapplication.data_layer.repository.languages;

import com.danielkashin.yandextestapplication.data_layer.entities.supported_languages.local.DatabaseSupportedLanguage;
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

  @Override
  public LanguagePair getDefaultLanguages() {
    DatabaseSupportedLanguage original = localService.getDefaultOriginalLanguage();
    DatabaseSupportedLanguage translated = localService.getDefaultTranslatedLanguage();

    Language originalLanguage = new Language(original.getCode(), original.getText());
    Language translatedLanguage = new Language(translated.getCode(), translated.getText());

    return new LanguagePair(originalLanguage, translatedLanguage);
  }

  @Override
  public LanguagePair getLanguages(String originalCode, String translatedCode) {
    DatabaseSupportedLanguage original = localService.getLanguage(originalCode);
    DatabaseSupportedLanguage translated = localService.getLanguage(translatedCode);

    Language originalLanguage = new Language(original.getCode(), original.getText());
    Language translatedLanguage = new Language(translated.getCode(), translated.getText());

    return new LanguagePair(originalLanguage, translatedLanguage);
  }

  @Override
  public Language getLanguage(String code) {
    DatabaseSupportedLanguage databaseSupportedLanguage = localService.getLanguage(code);

    return new Language(databaseSupportedLanguage.getCode(), databaseSupportedLanguage.getText());
  }

  @Override
  public ArrayList<Language> getAllLanguages() {
    ArrayList<DatabaseSupportedLanguage> databaseSupportedLanguages = localService.getAllLanguages();

    ArrayList<Language> languages = new ArrayList<>();
    for (DatabaseSupportedLanguage databaseSupportedLanguage : databaseSupportedLanguages) {
      languages.add(new Language(databaseSupportedLanguage.getCode(), databaseSupportedLanguage.getText()));
    }

    return languages;
  }


  public static class Factory {

    private Factory(){
    }

    public static ISupportedLanguagesRepository create(ISupportedLanguagesLocalService service){
      return new SupportedLanguagesRepository(service);
    }

  }
}
