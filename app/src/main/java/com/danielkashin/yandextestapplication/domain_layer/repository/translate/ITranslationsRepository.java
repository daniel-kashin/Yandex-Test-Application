package com.danielkashin.yandextestapplication.domain_layer.repository.translate;

import android.support.v4.util.Pair;

import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;

import java.util.ArrayList;

/*
 * Repository returns and receives data of the needed type, no matter what source it has.
 * connects UseCases/Interactors with Services
 */
public interface ITranslationsRepository {

  // --------------------------------------- delete -----------------------------------------------

  void deleteTranslation(Translation translation) throws ExceptionBundle;

  void deleteTranslations(boolean favorite) throws ExceptionBundle;

  // ----------------------------------------- put ------------------------------------------------

  void saveTranslation(Translation translation) throws ExceptionBundle;

  void refreshTranslation(Translation translation) throws ExceptionBundle;

  // ----------------------------------------- get ------------------------------------------------

  Translation getRefreshedTranslation(Translation translation) throws ExceptionBundle;

  Translation getLastTranslation() throws ExceptionBundle;

  Pair<Translation, Translation.Source> getTranslationAndItsSource(String originalText,
                                                                   String language) throws ExceptionBundle;

  ArrayList<Translation> getTranslations(int offset, int count, boolean onlyFavourite,
                                         String searchRequest) throws ExceptionBundle;
}