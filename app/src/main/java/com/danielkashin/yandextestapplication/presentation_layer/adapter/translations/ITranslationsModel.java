package com.danielkashin.yandextestapplication.presentation_layer.adapter.translations;

import android.os.Bundle;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import java.util.List;


public interface ITranslationsModel {

  void onSaveInstanceState(Bundle outState);

  void addTranslations(List<Translation> translations, boolean clear);

  void clear();

  int getSize();

  void addCallbacks(ITranslationsCallbacks callbacks);

  void removeCallbacks();

}
