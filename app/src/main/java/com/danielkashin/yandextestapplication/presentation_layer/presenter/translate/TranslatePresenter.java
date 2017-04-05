package com.danielkashin.yandextestapplication.presentation_layer.presenter.translate;

import android.util.Pair;

import com.danielkashin.yandextestapplication.R;
import com.danielkashin.yandextestapplication.data_layer.entities.local.DatabaseTranslation;
import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.data_layer.managers.network.INetworkManager;
import com.danielkashin.yandextestapplication.data_layer.managers.network.NetworkStatus;
import com.danielkashin.yandextestapplication.data_layer.managers.network.NetworkSubscriber;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.local.GetLastTranslationUseCase;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.remote.TranslateUseCase;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.IPresenterFactory;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.Presenter;
import com.danielkashin.yandextestapplication.presentation_layer.view.translate.ITranslateView;


public class TranslatePresenter extends Presenter<ITranslateView>
    implements TranslateUseCase.Callbacks, GetLastTranslationUseCase.Callbacks {

  private String mTextCache;

  private String mLangFrom;
  private String mLangTo;

  private final TranslateUseCase mTranslateUseCase;
  private final GetLastTranslationUseCase mGetLastTranslationUseCase;
  private final INetworkManager mNetworkManager;
  private NetworkSubscriber mTranslationOnInternetAvailable;


  public TranslatePresenter(TranslateUseCase translateUseCase,
                            GetLastTranslationUseCase getLastTranslationUseCase,
                            INetworkManager manager) {

    mTranslateUseCase = translateUseCase;
    mGetLastTranslationUseCase = getLastTranslationUseCase;
    mNetworkManager = manager;
  }


  public void onDataWasNotRestored() {
    // mGetLastTranslationUseCase.run(this);
  }

  public void onInputTextClear() {
    mTranslateUseCase.cancel();
    disposeTranslationSubscription();

    if (getView() != null) {
      getView().hideProgressBar();
      getView().hideNoInternet();
      getView().setTranslatedText("");
    }
  }

  public void onInputTextChanged(final String originalText) {
    mTranslateUseCase.cancel();
    disposeTranslationSubscription();

    if (getView() != null) {
      getView().showProgressBar();
      getView().hideNoInternet();
    }

    if (mNetworkManager.getCurrentNetworkStatus() != NetworkStatus.DISCONNECTED) {
      mTranslateUseCase.run(this, originalText, "ru-en");
    } else {
      if (getView() != null) {
        getView().setTranslatedText("");
        getView().showNoInternet();
      }

      subscribeTranslationOnNetworkAvailable(originalText);
    }
  }

  // ----------------------------------- Presenter lifecycle --------------------------------------

  @Override
  protected void onViewAttached() {
    if (mTextCache != null && !mTextCache.equals("") && getView() != null){
      getView().setTranslatedText(mTextCache);
      mTextCache = "";
    }

    if (getView() != null ) {
      if (mTranslateUseCase.isRunning()) {
        getView().showProgressBar();
      }

      if (mNetworkManager.getCurrentNetworkStatus() == NetworkStatus.DISCONNECTED
          && mTranslationOnInternetAvailable != null
          && !mTranslationOnInternetAvailable.isDisposed()){
        getView().showProgressBar();
        getView().showNoInternet();
      }
    }
  }

  @Override
  protected void onViewDetached() {
    if (getView() != null){
      getView().removeTextWatcher();
    }
  }

  @Override
  protected void onDestroyed() {
    mTranslateUseCase.cancel();

    // TODO: remove
    if (mGetLastTranslationUseCase != null)
      mGetLastTranslationUseCase.cancel();

    disposeTranslationSubscription();
  }

  // ---------------------------- GetLastTranslationUseCase callbacks -----------------------------

  @Override
  public void onGetLastTranslationResult(Translation translation) {
    if (getView() != null) {
      getView().setInputText(translation.getOriginalText());
      getView().setTranslatedText(translation.getTranslatedText());
      getView().setTextWatcher();
    }
  }

  @Override
  public void onGetLastTranslationError(ExceptionBundle exception) {
    if (getView() != null) {
      getView().setTextWatcher();
    }
  }

  // -------------------------------- TranslateUseCase callbacks ----------------------------------

  @Override
  public void onTranslateSuccess(Translation translation) {
    disposeTranslationSubscription();

    if (getView() != null) {
      getView().hideProgressBar();
      getView().setTranslatedText(translation.getTranslatedText());
    } else {
      mTextCache = translation.getTranslatedText();
    }
  }

  @Override
  public void onTranslateError(Pair<String, ExceptionBundle> pairOriginalTextException) {
    disposeTranslationSubscription();

    if (getView() != null) {
      getView().hideProgressBar();

      switch (pairOriginalTextException.second.getReason()) {
        case NETWORK_UNAVAILABLE:
          getView().setTranslatedText("");
          getView().showNoInternet();
          getView().showProgressBar();
          subscribeTranslationOnNetworkAvailable(pairOriginalTextException.first);
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

  // ------------------------------------ private methods ----------------------------------------

  private void subscribeTranslationOnNetworkAvailable(final String originalText) {
    mTranslationOnInternetAvailable = new NetworkSubscriber() {
      @Override
      public void onResult(NetworkStatus networkStatus) {
        if (networkStatus == NetworkStatus.CONNECTED) {
          onInputTextChanged(originalText);
        }
      }
    };

    mNetworkManager.registerSubscriber(mTranslationOnInternetAvailable);
  }

  private void disposeTranslationSubscription(){
    if (mTranslationOnInternetAvailable != null) {
      mTranslationOnInternetAvailable.dispose();
      mTranslationOnInternetAvailable = null;
    }
  }

  // ------------------------------------- Inner classes -----------------------------------------

  public static final class Factory
      implements IPresenterFactory<TranslatePresenter, ITranslateView> {

    private final TranslateUseCase translateUseCase;
    private final GetLastTranslationUseCase getLastTranslationUseCase;
    private final INetworkManager networkManager;

    public Factory(TranslateUseCase translateUseCase,
                   GetLastTranslationUseCase getLastTranslationUseCase,
                   INetworkManager networkManager){

      this.translateUseCase = translateUseCase;
      this.getLastTranslationUseCase = getLastTranslationUseCase;
      this.networkManager = networkManager;
    }

    @Override
    public TranslatePresenter create() {
      return new TranslatePresenter(translateUseCase, getLastTranslationUseCase, networkManager);
    }

  }

}
