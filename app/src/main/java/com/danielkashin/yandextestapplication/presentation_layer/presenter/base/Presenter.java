package com.danielkashin.yandextestapplication.presentation_layer.presenter.base;

import com.danielkashin.yandextestapplication.presentation_layer.view.base.IView;


public abstract class Presenter<V extends IView> implements IPresenter<V> {

  private V view;

  // ----------------------------------------------------------------------------------------------

  protected final V getView() {
    return this.view;
  }

  // --------------------------------- IPresenter methods -----------------------------------------

  @Override
  public void attachView(V view) {
    this.view = view;
    this.onViewAttached();
  }

  @Override
  public void detachView() {
    this.onViewDetached();
    this.view = null;
  }

  @Override
  public void destroy() {
    this.onDestroyed();
  }

  // ----------------------------------- abstract methods -----------------------------------------

  protected abstract void onViewDetached();

  protected abstract void onViewAttached();

  protected abstract void onDestroyed();
}
