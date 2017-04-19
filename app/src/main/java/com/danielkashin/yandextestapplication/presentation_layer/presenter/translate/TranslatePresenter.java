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

  private final TranslateUseCase mTranslateUseCase;
  private final GetLastTranslationUseCase mGetLastTranslationUseCase;
  private final INetworkManager mNetworkManager;

  private NetworkSubscriber mTranslationOnInternetAvailable;
  private String mTextCache;
  private boolean mCacheTranslationSaved;
  private LanguagePair mLanguageCache;


  public TranslatePresenter(TranslateUseCase translateUseCase,
                            GetLastTranslationUseCase getLastTranslationUseCase,
                            INetworkManager manager) {
    mTranslateUseCase = translateUseCase;
    mGetLastTranslationUseCase = getLastTranslationUseCase;
    mNetworkManager = manager;
  }

  // ----------------------------------- Presenter lifecycle --------------------------------------

  @Override
  protected void onViewAttached() {
    mLanguageCache = null;

    if (mTextCache != null && !mTextCache.equals("")) {
      getView().setTranslatedText(mTextCache);
      mTextCache = "";
    }

    if (mCacheTranslationSaved) {
      getView().onTranslationSaved();
      mCacheTranslationSaved = false;
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
    mLanguageCache = getView().getLanguages();
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
    if (getView() != null) {
      getView().initializeLanguages(result.second);
      getView().setInputText(result.first.getOriginalText());
      getView().setTranslatedText(result.first.getTranslatedText());
      getView().showImageClear();
      getView().setTextWatcher();
      getView().setSwapLanguagesListener();
    }
  }

  @Override
  public void onGetLastTranslationException(Pair<ExceptionBundle, LanguagePair> result) {
    if (getView() != null) {
      getView().initializeLanguages(result.second);
      getView().hideImageClear();
      getView().setTextWatcher();
      getView().setSwapLanguagesListener();
    }
  }

  // -------------------------------- TranslateUseCase callbacks ----------------------------------


  @Override
  public void onSaveTranslationSuccess() {
    if (getView() != null) {
      getView().onTranslationSaved();
    } else {
      mCacheTranslationSaved = true;
    }
  }

  @Override
  public void onSaveTranslationException(ExceptionBundle exceptionBundle) {
    // TODO
  }

  @Override
  public void onTranslateSuccess(Translation translation) {
    disposeTranslationSubscription();

    if (getView() != null) {
      getView().hideProgressBar();
      getView().hideNoInternet();
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

  public void onNotFirstStart() {
    if (getView() != null) {
      getView().setTextWatcher();
      getView().setSwapLanguagesListener();
    }
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

    if (getView() != null) {
      getView().showProgressBar();
    }

    //if (mNetworkManager.getCurrentNetworkStatus() != NetworkStatus.DISCONNECTED) {
      if (getView() != null) {
        mTranslateUseCase.run(this, originalText, getView().getLanguages().getLanguageCodePair());
      } else {
        mTranslateUseCase.run(this, originalText, mLanguageCache.getLanguageCodePair());
      }
    //} else {
    //  if (getView() != null) {
    //    getView().setTranslatedText("");
    //    getView().showNoInternet();
    //  }
    //
    //  disposeTranslationSubscription();
    //  subscribeTranslationOnNetworkAvailable(originalText);
    //}
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

// --------------------------------------- inner types --------------------------------------------

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
