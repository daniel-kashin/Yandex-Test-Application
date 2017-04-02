package com.danielkashin.yandextestapplication.presentation_layer.view.history_all;


import android.view.View;

import com.danielkashin.yandextestapplication.R;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.IPresenterFactory;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.history_all.HistoryAllPresenter;
import com.danielkashin.yandextestapplication.presentation_layer.view.base.PresenterFragment;

public class HistoryAllFragment
    extends PresenterFragment<HistoryAllPresenter, IHistoryAllView>
    implements IHistoryAllView {


  public static HistoryAllFragment getInstance() {
    return new HistoryAllFragment();
  }

  @Override
  protected IHistoryAllView getViewInterface() {
    return this;
  }

  @Override
  protected IPresenterFactory<HistoryAllPresenter, IHistoryAllView> getPresenterFactory() {
    return new HistoryAllPresenter.Factory();
  }

  @Override
  protected int getFragmentId() {
    return "HistoryAllFragment".hashCode();
  }

  @Override
  protected int getLayoutRes() {
    return R.layout.fragment_history_all;
  }

  @Override
  protected void initializeView(View view) {

  }

  @Override
  protected void setListeners() {

  }

}
