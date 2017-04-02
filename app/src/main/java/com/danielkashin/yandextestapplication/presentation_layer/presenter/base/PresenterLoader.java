package com.danielkashin.yandextestapplication.presentation_layer.presenter.base;

import android.content.Context;
import android.support.v4.content.Loader;

import com.danielkashin.yandextestapplication.presentation_layer.view.base.IView;


public class PresenterLoader<P extends Presenter<V>, V extends IView> extends Loader<P> {

  private final IPresenterFactory<P, V> factory;
  private P presenter;


  public PresenterLoader(Context context, IPresenterFactory<P, V> factory){
    super(context);
    this.factory = factory;
  }


  // ---------------------------------- public methods --------------------------------------------

  public final P getPresenter(){
    return presenter;
  }

  // ---------------------------------- Loader methods --------------------------------------------

  @Override
  protected void onStartLoading() {
    if (presenter != null) {
      int i = this.hashCode();
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
    if (presenter != null) {
      presenter.destroy();
    }

    presenter = null;
  }
}
