package com.danielkashin.yandextestapplication.presentation_layer.presenter.history_favourite;


import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.IPresenterFactory;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.Presenter;
import com.danielkashin.yandextestapplication.presentation_layer.view.history_favourite.IHistoryFavouriteView;

public class HistoryFavouritePresenter extends Presenter<IHistoryFavouriteView> {

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
      implements IPresenterFactory<HistoryFavouritePresenter, IHistoryFavouriteView> {

    @Override
    public HistoryFavouritePresenter create() {
      return new HistoryFavouritePresenter();
    }

  }

}
