package com.danielkashin.yandextestapplication.domain_layer.repository.languages;

import com.danielkashin.yandextestapplication.domain_layer.pojo.Language;
import com.danielkashin.yandextestapplication.domain_layer.pojo.LanguagePair;

import java.util.ArrayList;


public interface ILanguagesRepository {

  LanguagePair getLanguages(String originalCode, String translatedCode);

  LanguagePair getDefaultLanguages();

  Language getLanguage(String code);

  ArrayList<Language> getAllLanguages();

}
