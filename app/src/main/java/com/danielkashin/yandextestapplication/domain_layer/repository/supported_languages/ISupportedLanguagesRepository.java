package com.danielkashin.yandextestapplication.domain_layer.repository.supported_languages;

import android.support.annotation.NonNull;

import com.danielkashin.yandextestapplication.data_layer.entities.supported_languages.local.DatabaseLanguage;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Language;
import com.danielkashin.yandextestapplication.domain_layer.pojo.LanguagePair;

import java.util.ArrayList;


public interface ISupportedLanguagesRepository {

  @NonNull
  LanguagePair getLanguages(String originalCode, String translatedCode);

  @NonNull
  LanguagePair getDefaultLanguages();

  Language getLanguage(String code);

  ArrayList<Language> getAllLanguages();

}
