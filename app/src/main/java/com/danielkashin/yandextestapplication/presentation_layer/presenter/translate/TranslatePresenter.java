package com.danielkashin.yandextestapplication.presentation_layer.presenter.translate;

import android.support.v4.util.Pair;

import com.danielkashin.yandextestapplication.R;
import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.data_layer.managers.network.INetworkManager;
import com.danielkashin.yandextestapplication.data_layer.managers.network.NetworkStatus;
import com.danielkashin.yandextestapplication.data_layer.managers.network.NetworkSubscriber;
import com.danielkashin.yandextestapplication.domain_layer.pojo.LanguagePair;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.GetLastTranslationUseCase;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.SetTranslationFavoriteUseCase;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.TranslateUseCase;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.IPresenterFactory;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.Presenter;
import com.danielkashin.yandextestapplication.presentation_layer.view.translate.ITranslateView;


public class TranslatePresenter extends Presenter<ITranslateView>
    implements TranslateUseCase.Callbacks, GetLastTranslationUseCase.Callbacks,
    SetTranslationFavoriteUseCase.Callbacks {

  private final TranslateUseCase mTranslateUseCase;
  private final GetLastTranslationUseCase mGetLastTranslationUseCase;
  private final SetTranslationFavoriteUseCase mSetTranslationFavoriteUseCase;
  private final INetworkManager mNetworkManager;

  private NetworkSubscriber mTranslationOnInternetAvailable;
  private Translation mCachedTranslation;
  private boolean mCachedTranslationSaved;
  private boolean mCachedTranslationFavoriteChanged;
  private LanguagePair mLanguageCache;


  private TranslatePresenter(TranslateUseCase translateUseCase,
                             GetLastTranslationUseCase getLastTranslationUseCase,
                             SetTranslationFavoriteUseCase setTranslationFavoriteUseCase,
                             INetworkManager manager) {
    if (translateUseCase == null || getLastTranslationUseCase == null
        || manager == null || setTranslationFavoriteUseCase == null) {
      throw new IllegalArgumentException("All presenter arguments must be non null");
    }

    mTranslateUseCase = translateUseCase;
    mGetLastTranslationUseCase = getLastTranslationUseCase;
    mSetTranslationFavoriteUseCase = setTranslationFavoriteUseCase;
    mNetworkManager = manager;
  }

  // ----------------------------------- Presenter lifecycle --------------------------------------

  @Override
  protected void onViewAttached() {
    mLanguageCache = null;

    if (mCachedTranslation != null) {
      getView().setTranslationData(mCachedTranslation.getOriginalText(), mCachedTranslation.ifFavorite());
      getView().setToggleFavoriteListener();
      mCachedTranslation = null;
    }

    if (mCachedTranslationSaved) {
      getView().onTranslationSaved();
      mCachedTranslationSaved = false;
    }

    if (mCachedTranslationFavoriteChanged) {
      getView().onTranslationFavoriteChanged();
      mCachedTranslationFavoriteChanged = false;
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
    mLanguageCache = getView().getLanguagesIfInitialized();
    getView().removeTextWatcher();
    getView().removeSwapLanguagesListener();
    getView().removeToggleFavoriteListener();
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
      getView().setTranslationData(result.first.getTranslatedText(), result.first.ifFavorite());
      getView().showImageClear();
      getView().setTextWatcher();
      getView().setSwapLanguagesListener();
      getView().setToggleFavoriteListener();
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
      mCachedTranslationSaved = true;
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
      getView().setTranslationData(translation.getTranslatedText(), translation.ifFavorite());
      getView().setToggleFavoriteListener();
    } else {
      mCachedTranslation = translation;
    }
  }

  @Override
  public void onTranslateException(Pair<ExceptionBundle, String> pairOriginalTextException) {
    disposeTranslationSubscription();

    if (pairOriginalTextException.first.getReason() == ExceptionBundle.Reason.NETWORK_UNAVAILABLE) {
      subscribeTranslationOnNetworkAvailable(pairOriginalTextException.second);

      if (getView() != null) {
        getView().hideTranslationLayout();
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

  // ------------------------- SetTranslationFavoriteUseCase callbacks ----------------------------

  @Override
  public void onSetTranslationFavoriteSuccess() {
    if (getView() != null) {
      getView().onTranslationFavoriteChanged();
    } else {
      mCachedTranslationFavoriteChanged = true;
    }
  }

  @Override
  public void onSetTranslationFavoriteException(ExceptionBundle exceptionBundle) {

  }

  // ------------------------------------- public methods -----------------------------------------


  public void onFirstStart() {
    mGetLastTranslationUseCase.run(this);
  }

  public void onNotFirstStart() {
    if (getView() != null) {
      getView().setTextWatcher();
      getView().setSwapLanguagesListener();
    }
  }

  public void onToggleCheckedChanged(String originalText, String translatedText,
                                     String languageCodePair, boolean favorite) {
    mSetTranslationFavoriteUseCase.run(this, originalText, translatedText, languageCodePair, favorite);
  }

  public void onInputTextClear() {
    mTranslateUseCase.cancel();
    disposeTranslationSubscription();

    if (getView() != null) {
      getView().hideProgressBar();
      getView().hideNoInternet();
      getView().hideTranslationLayout();
      getView().removeToggleFavoriteListener();
    }
  }

  public void onInputTextChanged(final String originalText) {
    mTranslateUseCase.cancel();

    if (getView() != null) {
      getView().showProgressBar();
    }

    if (getView() != null) {
      mTranslateUseCase.run(this, originalText, getView().getLanguages().getLanguageCodePair());
    } else {
      mTranslateUseCase.run(this, originalText, mLanguageCache.getLanguageCodePair());
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

// --------------------------------------- inner types --------------------------------------------

  public static final class Factory
      implements IPresenterFactory<TranslatePresenter, ITranslateView> {

    private final TranslateUseCase translateUseCase;
    private final GetLastTranslationUseCase getLastTranslationUseCase;
    private final SetTranslationFavoriteUseCase setTranslationFavoriteUseCase;
    private final INetworkManager networkManager;


    public Factory(TranslateUseCase translateUseCase,
                   GetLastTranslationUseCase getLastTranslationUseCase,
                   SetTranslationFavoriteUseCase setTranslationFavoriteUseCase,
                   INetworkManager networkManager) {
      this.translateUseCase = translateUseCase;
      this.getLastTranslationUseCase = getLastTranslationUseCase;
      this.setTranslationFavoriteUseCase = setTranslationFavoriteUseCase;
      this.networkManager = networkManager;
    }

    @Override
    public TranslatePresenter create() {
      return new TranslatePresenter(translateUseCase,
          getLastTranslationUseCase,
          setTranslationFavoriteUseCase,
          networkManager);
    }
  }
}
