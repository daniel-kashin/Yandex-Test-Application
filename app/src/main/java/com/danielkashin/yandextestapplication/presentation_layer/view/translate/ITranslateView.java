package com.danielkashin.yandextestapplication.presentation_layer.view.translate;


import android.text.TextWatcher;

import com.danielkashin.yandextestapplication.presentation_layer.view.base.IView;

public interface ITranslateView extends IView {

  void removeInputTextListener(TextWatcher textWatcher);

  void setInputTextListener(TextWatcher textWatcher);

  void setInputText(String text);

  void setTranslatedText(String text);

  void showAlertDialog(String text);

  String getStringById(int id);

  void showProgressBar();

  void hideProgressBar();
}
