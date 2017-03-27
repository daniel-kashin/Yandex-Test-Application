package com.danielkashin.yandextestapplication.presentation_layer.presenter.translate;


import com.danielkashin.yandextestapplication.domain_layer.use_cases.TranslateUseCase;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.IPresenterFactory;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.Presenter;
import com.danielkashin.yandextestapplication.presentation_layer.view.translate.ITranslateView;

import okhttp3.ResponseBody;

public class TranslatePresenter extends Presenter<ITranslateView> implements TranslateUseCase.Callbacks {

  TranslateUseCase mTranslateUseCase;

  public TranslatePresenter(TranslateUseCase translateUseCase){
    mTranslateUseCase = translateUseCase;
  }


  @Override
  protected void onViewAttached() {

  }

  @Override
  protected void onViewDetached() {

  }

  @Override
  protected void onDestroyed() {
    mTranslateUseCase.cancel();
  }

  @Override
  public void onTranslateSuccess(ResponseBody result) {

  }

  @Override
  public void onTranslateError(Exception exception) {

  }

  public static final class TranslateFactory implements IPresenterFactory<TranslatePresenter, ITranslateView> {

    @Override
    public TranslatePresenter create() {
      return null;
    }

  }
}
