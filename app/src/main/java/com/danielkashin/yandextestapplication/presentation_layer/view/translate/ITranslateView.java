package com.danielkashin.yandextestapplication.presentation_layer.view.translate;


import com.danielkashin.yandextestapplication.domain_layer.pojo.Language;
import com.danielkashin.yandextestapplication.domain_layer.pojo.LanguagePair;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.base.IDatabaseChangePublisher;
import com.danielkashin.yandextestapplication.presentation_layer.view.base.IView;

public interface ITranslateView extends IView, IDatabaseChangePublisher {

  // --------------------------------------- languages --------------------------------------------

  LanguagePair getLanguages();

  void initializeLanguages(LanguagePair languages);

  void setOriginalLanguage(Language language);

  void setTranslatedLanguage(Language language);

  // ---------------------------------------- listeners -------------------------------------------

  void setSwapLanguagesListener();

  void removeSwapLanguagesListener();

  void setTextWatcher();

  void removeTextWatcher();

  // ------------------------------------- hide/show views ----------------------------------------

  void hideNoInternet();

  void showNoInternet();

  void showProgressBar();

  void hideProgressBar();

  void showImageClear();

  void hideImageClear();

  // ------------------------------------ other view handling -------------------------------------

  void setInputText(String text);

  void setTranslatedText(String text);

  void showAlertDialog(String text);

  String getStringById(int id);

}
