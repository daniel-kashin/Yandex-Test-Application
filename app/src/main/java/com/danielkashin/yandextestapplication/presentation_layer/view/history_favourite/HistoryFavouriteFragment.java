package com.danielkashin.yandextestapplication.presentation_layer.view.history_favourite;


import android.view.View;

import com.danielkashin.yandextestapplication.R;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.IPresenterFactory;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.history_favourite.HistoryFavouritePresenter;
import com.danielkashin.yandextestapplication.presentation_layer.view.base.PresenterFragment;

public class HistoryFavouriteFragment
    extends PresenterFragment<HistoryFavouritePresenter, IHistoryFavouriteView>
    implements IHistoryFavouriteView {


  public static HistoryFavouriteFragment getInstance() {
    return new HistoryFavouriteFragment();
  }

  @Override
  protected IHistoryFavouriteView getViewInterface() {
    return this;
  }

  @Override
  protected IPresenterFactory<HistoryFavouritePresenter, IHistoryFavouriteView> getPresenterFactory() {
    return new HistoryFavouritePresenter.Factory();
  }

  @Override
  protected int getFragmentId() {
    return "HistoryFavouriteFragment".hashCode();
  }

  @Override
  protected int getLayoutRes() {
    return R.layout.fragment_history_favourite;
  }

  @Override
  protected void initializeView(View view) {

  }

  @Override
  protected void setListeners() {

  }
}
