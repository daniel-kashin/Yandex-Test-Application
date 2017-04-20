package com.danielkashin.yandextestapplication.domain_layer.repository.translate;


import android.support.v4.util.Pair;

import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;

import java.util.ArrayList;
import java.util.List;


public interface ITranslationsRepository {

  // ------------------------------------ translations --------------------------------------------

  void deleteTranslations(boolean favorite) throws ExceptionBundle;

  void saveTranslation(Translation translation) throws ExceptionBundle;

  Pair<Translation, Translation.Source> getTranslationAndItsSource(String originalText, String language)
      throws ExceptionBundle;

  Translation getLastTranslation() throws ExceptionBundle;

  ArrayList<Translation> getTranslations(int offset, int count, boolean onlyFavourite,
                                         String searchRequest) throws ExceptionBundle;

}