package com.danielkashin.yandextestapplication.presentation_layer.presenter.translate;

import android.support.annotation.NonNull;
import android.util.Pair;
import com.danielkashin.yandextestapplication.R;
import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.data_layer.managers.network.INetworkManager;
import com.danielkashin.yandextestapplication.data_layer.managers.network.NetworkStatus;
import com.danielkashin.yandextestapplication.data_layer.managers.network.NetworkSubscriber;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.GetLastTranslationUseCase;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.TranslateUseCase;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.IPresenterFactory;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.Presenter;
import com.danielkashin.yandextestapplication.presentation_layer.view.translate.ITranslateView;


public class TranslatePresenter extends Presenter<ITranslateView>
    implements TranslateUseCase.Callbacks, GetLastTranslationUseCase.Callbacks {

  private String mTextCache;
  private String mLangFrom;
  private String mLangTo;

  private NetworkSubscriber mTranslationOnInternetAvailable;

  @NonNull
  private final TranslateUseCase mTranslateUseCase;
  @NonNull
  private final GetLastTranslationUseCase mGetLastTranslationUseCase;
  @NonNull
  private final INetworkManager mNetworkManager;


  public TranslatePresenter(@NonNull TranslateUseCase translateUseCase,
                            @NonNull GetLastTranslationUseCase getLastTranslationUseCase,
                            @NonNull INetworkManager manager) {

    mTranslateUseCase = translateUseCase;
    mGetLastTranslationUseCase = getLastTranslationUseCase;
    mNetworkManager = manager;
  }

  // ----------------------------------- Presenter lifecycle --------------------------------------

  @Override
  protected void onViewAttached() {
    if (mTextCache != null && !mTextCache.equals("") && getView() != null) {
      getView().setTranslatedText(mTextCache);
      mTextCache = "";
    }

    if (getView() != null) {
      if (mTranslateUseCase.isRunning()) {
        getView().showProgressBar();
      }

      if (mTranslationOnInternetAvailable != null && !mTranslationOnInternetAvailable.isDisposed()) {
        getView().showProgressBar();
        getView().showNoInternet();
      }
    }
  }

  @Override
  protected void onViewDetached() {
    if (getView() != null) {
      getView().removeTextWatcher();
    }
  }

  @Override
  protected void onDestroyed() {
    mTranslateUseCase.cancel();
    mGetLastTranslationUseCase.cancel();

    disposeTranslationSubscription();
  }

  // ---------------------------- GetLastTranslationUseCase callbacks -----------------------------

  @Override
  public void onGetLastTranslationSuccess(Translation translation) {
    if (getView() != null) {
      getView().setInputText(translation.getOriginalText());
      getView().setTranslatedText(translation.getTranslatedText());
      getView().setTextWatcher();
    }
  }

  @Override
  public void onGetLastTranslationException(ExceptionBundle exception) {
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
  public void onTranslateException(Pair<String, ExceptionBundle> pairOriginalTextException) {
    disposeTranslationSubscription();

    if (getView() != null) {
      switch (pairOriginalTextException.second.getReason()) {
        case NETWORK_UNAVAILABLE:
          getView().setTranslatedText("");
          getView().showNoInternet();
          getView().showProgressBar();
          subscribeTranslationOnNetworkAvailable(pairOriginalTextException.first);
          break;

        case WRONG_KEY:
          getView().showAlertDialog(getView().getStringById(R.string.wrong_key));
          getView().hideProgressBar();
          break;

        case LIMIT_EXPIRED:
          getView().showAlertDialog(getView().getStringById(R.string.limit_expired));
          getView().hideProgressBar();
          break;

        case TEXT_LIMIT_EXPIRED:
          getView().showAlertDialog(getView().getStringById(R.string.text_limit_expired));
          getView().hideProgressBar();
          break;

        case WRONG_TEXT:
          getView().showAlertDialog(getView().getStringById(R.string.wrong_text));
          getView().hideProgressBar();
          break;

        case WRONG_LANGS:
          getView().showAlertDialog(getView().getStringById(R.string.wrong_langs));
          getView().hideProgressBar();
          break;

        case UNKNOWN:
          getView().showAlertDialog(getView().getStringById(R.string.unknown));
          getView().hideProgressBar();
          break;

        default:
          getView().hideProgressBar();
          break;
      }
    }
  }

  // ------------------------------------ public methods -----------------------------------------



  public void onDataWasNotRestored() {
    mGetLastTranslationUseCase.run(this);
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

  private void disposeTranslationSubscription() {
    if (mTranslationOnInternetAvailable != null) {
      mTranslationOnInternetAvailable.dispose();
      mTranslationOnInternetAvailable = null;
    }
  }

  // ------------------------------------- Inner classes -----------------------------------------

  public static final class Factory
      implements IPresenterFactory<TranslatePresenter, ITranslateView> {

    @NonNull
    private final TranslateUseCase translateUseCase;
    @NonNull
    private final GetLastTranslationUseCase getLastTranslationUseCase;
    @NonNull
    private final INetworkManager networkManager;


    public Factory(@NonNull TranslateUseCase translateUseCase,
                   @NonNull GetLastTranslationUseCase getLastTranslationUseCase,
                   @NonNull INetworkManager networkManager) {
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
