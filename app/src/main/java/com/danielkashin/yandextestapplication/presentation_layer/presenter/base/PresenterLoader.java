package com.danielkashin.yandextestapplication.presentation_layer.presenter.base;

import android.content.Context;
import android.support.v4.content.Loader;


public class PresenterLoader<T extends IPresenter> extends Loader<T> {

  private final IPresenterFactory<T> factory;
  private T presenter;


  public PresenterLoader(Context context, IPresenterFactory<T> factory){
    super(context);
    this.factory = factory;
  }


  @Override
  protected void onStartLoading() {
    if (presenter != null) {
      deliverResult(presenter);
    } else {
      forceLoad();
    }
  }

  @Override
  protected void onForceLoad() {
    presenter = factory.create();
    deliverResult(presenter);
  }

  @Override
  protected void onReset() {
    presenter.onDestroyed();
    presenter = null;
  }
}
