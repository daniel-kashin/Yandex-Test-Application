package com.danielkashin.yandextestapplication.domain_layer.repository.supported_languages;

import com.danielkashin.yandextestapplication.data_layer.entities.supported_languages.local.Language;
import com.danielkashin.yandextestapplication.domain_layer.pojo.LanguagePair;

import java.util.ArrayList;


public interface ISupportedLanguagesRepository {

  LanguagePair getLanguages(String originalCode, String translatedCode);

  LanguagePair getDefaultLanguages();

  Language getLanguage(String code);

  ArrayList<Language> getAllLanguages();

}
