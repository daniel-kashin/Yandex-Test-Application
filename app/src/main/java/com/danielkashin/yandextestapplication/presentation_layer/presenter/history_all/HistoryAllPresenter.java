package com.danielkashin.yandextestapplication.presentation_layer.presenter.history_all;


import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.IPresenterFactory;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.Presenter;
import com.danielkashin.yandextestapplication.presentation_layer.view.history_all.IHistoryAllView;

public class HistoryAllPresenter extends Presenter<IHistoryAllView> {


  @Override
  protected void onViewDetached() {

  }

  @Override
  protected void onViewAttached() {

  }

  @Override
  protected void onDestroyed() {

  }


  // ------------------------------------- Inner classes -----------------------------------------

  public static final class Factory
      implements IPresenterFactory<HistoryAllPresenter, IHistoryAllView> {

    @Override
    public HistoryAllPresenter create() {
      return new HistoryAllPresenter();
    }

  }

}
