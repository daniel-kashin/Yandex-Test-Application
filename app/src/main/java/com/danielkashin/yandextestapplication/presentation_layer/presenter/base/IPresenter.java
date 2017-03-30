package com.danielkashin.yandextestapplication.presentation_layer.presenter.base;


public interface IPresenter<V> {

  void attachView(V view);

  void detachView();

  void destroy();

}
