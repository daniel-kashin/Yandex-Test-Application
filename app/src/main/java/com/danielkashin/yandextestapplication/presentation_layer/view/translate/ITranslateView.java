package com.danielkashin.yandextestapplication.presentation_layer.view.translate;


import android.text.TextWatcher;

import com.danielkashin.yandextestapplication.presentation_layer.view.base.IView;

public interface ITranslateView extends IView {

  void setTextWatcher();

  void removeTextWatcher();


  void hideNoInternet();

  void showNoInternet();


  void showProgressBar();

  void hideProgressBar();


  void showImageClear();

  void hideImageClear();


  void setOriginalLanguage(String originalLanguage);

  void setTranslatedLanguage(String translatedLanguage);


  void setInputText(String text);


  void setTranslatedText(String text);

  String getTranslatedText();


  void showAlertDialog(String text);


  String getStringById(int id);

}
