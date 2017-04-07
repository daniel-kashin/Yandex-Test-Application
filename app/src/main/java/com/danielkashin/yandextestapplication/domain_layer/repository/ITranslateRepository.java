package com.danielkashin.yandextestapplication.domain_layer.repository;

import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;

import java.util.List;


public interface ITranslateRepository {

  void saveTranslation(Translation translation) throws ExceptionBundle;

  Translation getTranslation(String originalText, String language) throws ExceptionBundle;

  Translation getLastTranslation() throws ExceptionBundle;

  List<Translation> getTranslations(int offset, int count, boolean onlyFavourite) throws ExceptionBundle;
}