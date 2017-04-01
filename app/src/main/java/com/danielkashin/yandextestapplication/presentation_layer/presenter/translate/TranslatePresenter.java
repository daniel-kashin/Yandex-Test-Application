package com.danielkashin.yandextestapplication.presentation_layer.presenter.translate;


import android.os.AsyncTask;

import com.danielkashin.yandextestapplication.R;
import com.danielkashin.yandextestapplication.data_layer.entities.remote.Translation;
import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.data_layer.services.remote.IYandexTranslateNetworkService;
import com.danielkashin.yandextestapplication.data_layer.services.remote.YandexTranslateNetworkService;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.YandexTranslateUseCase;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.IPresenterFactory;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.Presenter;
import com.danielkashin.yandextestapplication.presentation_layer.view.translate.ITranslateView;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class TranslatePresenter extends Presenter<ITranslateView> implements YandexTranslateUseCase.Callbacks {

  private final YandexTranslateUseCase mYandexTranslateUseCase;
  private String mLangFrom;
  private String mLangTo;


  public TranslatePresenter(YandexTranslateUseCase yandexTranslateUseCase){
    mYandexTranslateUseCase = yandexTranslateUseCase;
  }


  public void onInputTextClear() {
    if (getView() != null) {
      getView().hideProgressBar();
      getView().setResultText("");
    }
    mYandexTranslateUseCase.cancel();
  }

  public void onInputTextChanged(String text) {
    getView().showProgressBar();
    mYandexTranslateUseCase.run(this, text, "ru-en");
  }

  // ----------------------------------- Presenter lifecycle --------------------------------------

  @Override
  protected void onViewAttached() {
    if (getView() != null && mYandexTranslateUseCase.isRunning()) {
      getView().showProgressBar();
    }
  }

  @Override
  protected void onViewDetached() { }

  @Override
  protected void onDestroyed() {
    mYandexTranslateUseCase.cancel();
  }

  // -------------------------------- YandexTranslateUseCase callbacks ----------------------------------

  @Override
  public void onTranslateSuccess(Translation result) {
    if (getView() != null) {
      getView().hideProgressBar();
      getView().setResultText(result.getText());
    }
  }

  @Override
  public void onTranslateError(ExceptionBundle exception) {
    if (getView() != null) {
      getView().hideProgressBar();

      switch (exception.getReason()) {
        case NETWORK_UNAVAILABLE:
          break;
        case WRONG_KEY:
          getView().showAlertDialog(getView().getStringById(R.string.wrong_key));
          break;
        case LIMIT_EXPIRED:
          getView().showAlertDialog(getView().getStringById(R.string.limit_expired));
          break;
        case TEXT_LIMIT_EXPIRED:
          getView().showAlertDialog(getView().getStringById(R.string.text_limit_expired));
          break;
        case WRONG_TEXT:
          getView().showAlertDialog(getView().getStringById(R.string.wrong_text));
          break;
        case WRONG_LANGS:
          getView().showAlertDialog(getView().getStringById(R.string.wrong_langs));
          break;
        case UNKNOWN:
          getView().showAlertDialog(getView().getStringById(R.string.unknown));
          break;
        default:
          break;
      }
    }
  }


  // ------------------------------------- Inner classes -----------------------------------------

  public static final class TranslateFactory
      implements IPresenterFactory<TranslatePresenter, ITranslateView> {

    @Override
    public TranslatePresenter create() {
      // bind networkService
      OkHttpClient okHttpClient = new OkHttpClient.Builder()
          .readTimeout(10, TimeUnit.SECONDS)
          .connectTimeout(10, TimeUnit.SECONDS)
          .build();
      IYandexTranslateNetworkService networkService =
          YandexTranslateNetworkService.Factory.create(okHttpClient);

      // bind useCase
      YandexTranslateUseCase useCase = new YandexTranslateUseCase(AsyncTask.THREAD_POOL_EXECUTOR, networkService);

      return new TranslatePresenter(useCase);
    }

  }

}
