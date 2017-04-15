package com.danielkashin.yandextestapplication.data_layer.services.supported_languages.local;

import com.danielkashin.yandextestapplication.data_layer.entities.supported_languages.local.Language;

import java.util.ArrayList;


public interface ISupportedLanguagesLocalService {

  public Language getDefaultOriginalLanguage();

  public Language getDefaultTranslatedLanguage();

  public Language getLanguage(String code);

  public ArrayList<Language> getAllLanguages();

}
