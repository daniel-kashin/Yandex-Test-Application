package com.danielkashin.yandextestapplication.presentation_layer.view.translate;


import com.danielkashin.yandextestapplication.presentation_layer.view.base.IView;

public interface ITranslateView extends IView {

  void setResultText(String text);

  void showAlertDialog(String text);

  String getStringById(int id);

  void showProgressBar();

  void hideProgressBar();
}
