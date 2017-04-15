package com.danielkashin.yandextestapplication.presentation_layer.view.translate;


import android.text.TextWatcher;

import com.danielkashin.yandextestapplication.data_layer.entities.supported_languages.local.Language;
import com.danielkashin.yandextestapplication.domain_layer.pojo.LanguagePair;
import com.danielkashin.yandextestapplication.presentation_layer.view.base.IView;

public interface ITranslateView extends IView {

  LanguagePair getLanguages();

  void swapLanguages();

  void initializeLanguages(LanguagePair languages);

  void setOriginalLanguage(Language language);

  void setTranslatedLanguage(Language language);


  void setTextWatcher();

  void removeTextWatcher();


  void hideNoInternet();

  void showNoInternet();


  void showProgressBar();

  void hideProgressBar();


  void showImageClear();

  void hideImageClear();


  void setInputText(String text);


  void setTranslatedText(String text);

  String getTranslatedText();


  void showAlertDialog(String text);


  String getStringById(int id);

}
