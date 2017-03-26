package com.danielkashin.yandextestapplication.presentation_layer.presenter.base;


public interface IPresenter<T> {

  void attachView(T view);

  void detachView();

  void destroy();

}
