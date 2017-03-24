package com.danielkashin.yandextestapplication.presentation_layer.presenter.base;


public interface IPresenterFactory<T extends IPresenter> {

  T create();

}