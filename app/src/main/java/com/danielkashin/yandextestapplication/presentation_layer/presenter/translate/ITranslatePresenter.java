package com.danielkashin.yandextestapplication.presentation_layer.presenter.translate;

import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;


public interface ITranslatePresenter {

  void onFirstStart();

  void onNotFirstStart();

  void onRefreshFavoriteValue(String originalText, String translatedText,
                              String languagePairText, boolean favorite);

  void onSetTranslationData(Translation translation);

  void onToggleCheckedChanged(String originalText, String translatedText,
                              String languageCodePair, boolean favorite);

  void onInputTextClear();

  void onInputTextChanged(final String originalText);

}
