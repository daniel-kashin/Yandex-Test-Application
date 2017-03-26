package com.danielkashin.yandextestapplication.presentation_layer.presenter.translate;


import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.IPresenterFactory;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.Presenter;
import com.danielkashin.yandextestapplication.presentation_layer.view.translate.ITranslateView;

public class TranslatePresenter extends Presenter<ITranslateView> {


  @Override
  protected void onViewDetached() {

  }

  @Override
  protected void onViewAttached() {

  }

  @Override
  protected void onDestroyed() {

  }

  public static final class TranslateFactory implements IPresenterFactory<TranslatePresenter, ITranslateView> {

    @Override
    public TranslatePresenter create() {
      return null;
    }
  }
}
