package com.danielkashin.yandextestapplication.presentation_layer.view.translate;


import com.danielkashin.yandextestapplication.domain_layer.pojo.Language;
import com.danielkashin.yandextestapplication.domain_layer.pojo.LanguagePair;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.base.IDatabaseChangePublisher;
import com.danielkashin.yandextestapplication.presentation_layer.view.base.IView;

public interface ITranslateView extends IView {

  // -------------------------------------- data change -------------------------------------------

  void onTranslationFavoriteChanged();

  void onTranslationSaved();

  // --------------------------------------- languages --------------------------------------------

  LanguagePair getLanguages();

  LanguagePair getLanguagesIfInitialized();

  void initializeLanguages(LanguagePair languages);

  void setOriginalLanguage(Language language);

  void setTranslatedLanguage(Language language);

  // ---------------------------------------- listeners -------------------------------------------

  void setToggleFavoriteListener();

  void removeToggleFavoriteListener();

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

  void hideTranslationLayout();

  // ------------------------------------ other view handling -------------------------------------

  void setInputText(String text);

  void setTranslationData(String text, boolean isFavorite);

  void showAlertDialog(String text);

  String getStringById(int id);

}
