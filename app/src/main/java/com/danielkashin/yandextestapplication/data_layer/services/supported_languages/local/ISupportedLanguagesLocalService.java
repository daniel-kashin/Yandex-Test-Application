package com.danielkashin.yandextestapplication.data_layer.services.supported_languages.local;

import com.danielkashin.yandextestapplication.data_layer.entities.supported_languages.local.DatabaseLanguage;

import java.util.ArrayList;


public interface ISupportedLanguagesLocalService {

  public DatabaseLanguage getDefaultOriginalLanguage();

  public DatabaseLanguage getDefaultTranslatedLanguage();

  public DatabaseLanguage getLanguage(String code);

  public ArrayList<DatabaseLanguage> getAllLanguages();

}
