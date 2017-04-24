package com.danielkashin.yandextestapplication.presentation_layer.presenter.base;

/*
 * basic presenter lifecycle callbacks, for initializing/detaching resources and other features
 */
public interface IPresenter<V> {

  void attachView(V view);

  void detachView();

  void destroy();

}
