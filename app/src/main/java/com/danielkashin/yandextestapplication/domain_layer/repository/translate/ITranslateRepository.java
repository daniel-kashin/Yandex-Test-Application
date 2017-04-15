package com.danielkashin.yandextestapplication.domain_layer.repository.translate;

import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;

import java.util.ArrayList;
import java.util.List;


public interface ITranslateRepository {

  // ------------------------------------ translations --------------------------------------------

  void saveTranslation(Translation translation) throws ExceptionBundle;

  Translation getTranslation(String originalText, String language) throws ExceptionBundle;

  Translation getLastTranslation() throws ExceptionBundle;

  ArrayList<Translation> getTranslations(int offset, int count, boolean onlyFavourite,
                                         String searchRequest) throws ExceptionBundle;

  // ------------------------------------- languages ----------------------------------------------

  String getLanguage(String languageCode);

}