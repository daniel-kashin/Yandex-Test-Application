package com.danielkashin.yandextestapplication.presentation_layer.presenter.base;


public interface IPresenter<T> {

  void onViewAttached(T view);

  void onViewDetached();

  void onDestroyed();

}
