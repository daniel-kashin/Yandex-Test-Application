package com.danielkashin.yandextestapplication.domain_layer.repository.languages;

import com.danielkashin.yandextestapplication.domain_layer.pojo.Language;
import com.danielkashin.yandextestapplication.domain_layer.pojo.LanguagePair;

import java.util.ArrayList;


/*
 * Repository returns and receives data of the needed type, no matter what source it has
 * connects UseCases/Interactors with Services
 */
public interface ISupportedLanguagesRepository {

  LanguagePair getLanguages(String originalCode, String translatedCode);

  LanguagePair getDefaultLanguages();

  Language getLanguage(String code);

  ArrayList<Language> getAllLanguages();

}
