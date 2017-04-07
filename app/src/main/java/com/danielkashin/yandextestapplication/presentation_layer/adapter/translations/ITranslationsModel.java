package com.danielkashin.yandextestapplication.presentation_layer.adapter.translations;

import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import java.util.List;


public interface ITranslationsModel {

  void addTranslations(List<Translation> translations);

  void clear();

  int getSize();
}
