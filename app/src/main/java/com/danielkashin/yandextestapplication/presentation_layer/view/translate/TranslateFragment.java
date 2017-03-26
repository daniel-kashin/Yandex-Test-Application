package com.danielkashin.yandextestapplication.presentation_layer.view.translate;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.danielkashin.yandextestapplication.R;
import com.danielkashin.yandextestapplication.data_layer.services.yandex_translate.YandexTranslateNetworkService;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.IPresenterFactory;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.translate.TranslatePresenter;
import com.danielkashin.yandextestapplication.presentation_layer.view.base.IView;
import com.danielkashin.yandextestapplication.presentation_layer.view.base.PresenterFragment;


public class TranslateFragment extends PresenterFragment<TranslatePresenter, ITranslateView>
    implements ITranslateView {


  @Override
  protected ITranslateView getViewInterface() {
    return this;
  }

  @Override
  protected IPresenterFactory<TranslatePresenter, ITranslateView> getPresenterFactory() {
    return new TranslatePresenter.TranslateFactory();
  }

  @Override
  protected int getFragmentId() {
    return "TranslateFragment".hashCode();
  }

  @Override
  protected int getLayoutRes() {
    return R.layout.fragment_translate;
  }

  @Override
  protected void initializeView(View view) {

  }
}
