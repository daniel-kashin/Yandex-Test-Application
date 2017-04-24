package com.danielkashin.yandextestapplication.data_layer.services.supported_languages.local;

import com.danielkashin.yandextestapplication.data_layer.entities.supported_languages.local.DatabaseSupportedLanguage;

import java.util.ArrayList;


public interface ISupportedLanguagesLocalService {

  public DatabaseSupportedLanguage getDefaultOriginalLanguage();

  public DatabaseSupportedLanguage getDefaultTranslatedLanguage();

  public DatabaseSupportedLanguage getLanguage(String code);

  public ArrayList<DatabaseSupportedLanguage> getAllLanguages();

}
