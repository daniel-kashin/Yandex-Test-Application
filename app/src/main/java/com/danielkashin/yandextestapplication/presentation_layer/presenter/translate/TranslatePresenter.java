package com.danielkashin.yandextestapplication.presentation_layer.presenter.translate;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.danielkashin.yandextestapplication.R;
import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.data_layer.managers.network.INetworkManager;
import com.danielkashin.yandextestapplication.data_layer.managers.network.NetworkStatus;
import com.danielkashin.yandextestapplication.data_layer.managers.network.NetworkSubscriber;
import com.danielkashin.yandextestapplication.domain_layer.pojo.LanguagePair;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.GetLastTranslationUseCase;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.TranslateUseCase;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.IPresenterFactory;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.Presenter;
import com.danielkashin.yandextestapplication.presentation_layer.view.translate.ITranslateView;


public class TranslatePresenter extends Presenter<ITranslateView>
    implements TranslateUseCase.Callbacks, GetLastTranslationUseCase.Callbacks {

  @NonNull
  private final TranslateUseCase mTranslateUseCase;
  @NonNull
  private final GetLastTranslationUseCase mGetLastTranslationUseCase;
  @NonNull
  private final INetworkManager mNetworkManager;

  private NetworkSubscriber mTranslationOnInternetAvailable;
  private String mTextCache;
  private LanguagePair mLanguages;


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
    if (mLanguages != null) {
      getView().setOriginalLanguage(mLanguages.getOriginalLanguage().getText());
      getView().setTranslatedLanguage(mLanguages.getTranslatedLanguage().getText());
    }

    if (mTextCache != null && !mTextCache.equals("")) {
      getView().setTranslatedText(mTextCache);
      mTextCache = "";
    }

    if (mTranslateUseCase.isRunning()) {
      getView().showProgressBar();
    }

    if (mTranslationOnInternetAvailable != null && !mTranslationOnInternetAvailable.isDisposed()) {
      getView().showProgressBar();
      getView().showNoInternet();
    }
  }

  @Override
  protected void onViewDetached() {
    getView().removeTextWatcher();
  }

  @Override
  protected void onDestroyed() {
    mTranslateUseCase.cancel();
    mGetLastTranslationUseCase.cancel();

    disposeTranslationSubscription();
  }

  // ---------------------------- GetLastTranslationUseCase callbacks -----------------------------

  @Override
  public void onGetLastTranslationSuccess(Pair<Translation, LanguagePair> result) {
    mLanguages = result.second;

    if (getView() != null) {
      getView().setInputText(result.first.getOriginalText());
      getView().setTranslatedText(result.first.getTranslatedText());
      getView().setTextWatcher();
      getView().showImageClear();
      getView().setOriginalLanguage(mLanguages.getOriginalLanguage().getText());
      getView().setTranslatedLanguage(mLanguages.getTranslatedLanguage().getText());
    }
  }

  @Override
  public void onGetLastTranslationException(Pair<ExceptionBundle, LanguagePair> result) {
    mLanguages = result.second;

    if (getView() != null) {
      getView().setTextWatcher();
      getView().hideImageClear();
      getView().setOriginalLanguage(mLanguages.getOriginalLanguage().getText());
      getView().setTranslatedLanguage(mLanguages.getTranslatedLanguage().getText());
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
  public void onTranslateException(Pair<ExceptionBundle, String> pairOriginalTextException) {
    disposeTranslationSubscription();

    if (pairOriginalTextException.first.getReason() == ExceptionBundle.Reason.NETWORK_UNAVAILABLE) {
      subscribeTranslationOnNetworkAvailable(pairOriginalTextException.second);

      if (getView() != null) {
        getView().setTranslatedText("");
        getView().showNoInternet();
        getView().showProgressBar();
      }
    } else {
      if (getView() != null) {
        getView().hideNoInternet();
        getView().hideProgressBar();

        String errorMessage = null;
        switch (pairOriginalTextException.first.getReason()) {
          case WRONG_KEY:
            errorMessage = getView().getStringById(R.string.wrong_key);
            break;
          case LIMIT_EXPIRED:
            errorMessage = getView().getStringById(R.string.limit_expired);
            break;
          case TEXT_LIMIT_EXPIRED:
            errorMessage = getView().getStringById(R.string.text_limit_expired);
            break;
          case WRONG_TEXT:
            errorMessage = getView().getStringById(R.string.wrong_text);
            break;
          case WRONG_LANGS:
            errorMessage = getView().getStringById(R.string.wrong_langs);
            break;
          case UNKNOWN:
            errorMessage = getView().getStringById(R.string.unknown);
            break;
        }
        if (errorMessage != null) {
          getView().showAlertDialog(errorMessage);
        }
      }
    }
  }

  // ------------------------------------ public methods -----------------------------------------


  public void onFirstStart() {
    mGetLastTranslationUseCase.run(this);
  }

  public void onInputTextClear() {
    mTranslateUseCase.cancel();
    disposeTranslationSubscription();

    getView().hideProgressBar();
    getView().hideNoInternet();
    getView().setTranslatedText("");
  }

  public void onInputTextChanged(final String originalText) {
    mTranslateUseCase.cancel();

    getView().showProgressBar();

    if (mNetworkManager.getCurrentNetworkStatus() != NetworkStatus.DISCONNECTED) {
      mTranslateUseCase.run(this, originalText, mLanguages.getLanguageCodePair());
    } else {
      getView().setTranslatedText("");
      getView().showNoInternet();

      disposeTranslationSubscription();
      subscribeTranslationOnNetworkAvailable(originalText);
    }
  }

  public void onChangeButtonsImageClicked() {
    if (mLanguages != null) {
      mLanguages.swapLanguages();

      getView().setOriginalLanguage(mLanguages.getOriginalLanguage().getText());
      getView().setTranslatedLanguage(mLanguages.getTranslatedLanguage().getText());

      getView().setInputText(getView().getTranslatedText());
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
