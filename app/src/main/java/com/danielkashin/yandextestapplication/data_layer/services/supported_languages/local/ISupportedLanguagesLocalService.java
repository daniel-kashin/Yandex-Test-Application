package com.danielkashin.yandextestapplication.data_layer.services.supported_languages.local;

import com.danielkashin.yandextestapplication.data_layer.entities.supported_languages.local.DatabaseSupportedLanguage;

import java.util.ArrayList;


public interface ISupportedLanguagesLocalService {

  DatabaseSupportedLanguage getDefaultOriginalLanguage();

  DatabaseSupportedLanguage getDefaultTranslatedLanguage();

  DatabaseSupportedLanguage getLanguage(String code);

  ArrayList<DatabaseSupportedLanguage> getAllLanguages();

}
