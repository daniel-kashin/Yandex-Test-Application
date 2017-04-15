package com.danielkashin.yandextestapplication.presentation_layer.view.history;

import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.danielkashin.yandextestapplication.presentation_layer.view.base.IView;
import java.util.List;


public interface IHistoryView extends IView {

  void addTranslationsToAdapter(List<Translation> translations, boolean clear);

  void showNotEmptyContentInterface();

  void showEmptyContentInterface();

  void showEmptySearchContentInterface();

  int getTranslationAdapterCount();

  void clearTranslationAdapter();
}