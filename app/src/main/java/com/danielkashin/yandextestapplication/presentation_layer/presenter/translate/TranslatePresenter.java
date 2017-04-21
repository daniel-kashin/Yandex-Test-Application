package com.danielkashin.yandextestapplication.presentation_layer.presenter.translate;

import android.support.v4.util.Pair;

import com.danielkashin.yandextestapplication.R;
import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.data_layer.managers.network.INetworkManager;
import com.danielkashin.yandextestapplication.data_layer.managers.network.NetworkStatus;
import com.danielkashin.yandextestapplication.data_layer.managers.network.NetworkSubscriber;
import com.danielkashin.yandextestapplication.domain_layer.pojo.LanguagePair;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.GetLanguagesFromTranslationUseCase;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.GetRefreshedTranslationUseCase;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.SaveTranslationUseCase;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.GetLastTranslationUseCase;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.TranslateUseCase;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.IPresenterFactory;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.Presenter;
import com.danielkashin.yandextestapplication.presentation_layer.view.translate.ITranslateView;


public class TranslatePresenter extends Presenter<ITranslateView>
    implements ITranslatePresenter, TranslateUseCase.Callbacks, GetLastTranslationUseCase.Callbacks,
    SaveTranslationUseCase.Callbacks, GetRefreshedTranslationUseCase.Callbacks,
    GetLanguagesFromTranslationUseCase.Callbacks {

  private final TranslateUseCase mTranslateUseCase;
  private final GetLastTranslationUseCase mGetLastTranslationUseCase;
  private final SaveTranslationUseCase mSaveTranslationUseCase;
  private final GetRefreshedTranslationUseCase mGetRefreshedTranslationUseCase;
  private final GetLanguagesFromTranslationUseCase mGetLanguagesFromTranslationUseCase;
  private final INetworkManager mNetworkManager;

  private NetworkSubscriber mTranslationOnInternetAvailable;
  private Translation mCachedTranslation;
  private boolean mCachedTranslationSaved;
  private boolean mCachedTranslationFavoriteChanged;
  private boolean mCachedSetFavoriteToggleToFalse;
  private LanguagePair mCachedLanguages;


  private TranslatePresenter(TranslateUseCase translateUseCase,
                             GetLastTranslationUseCase getLastTranslationUseCase,
                             SaveTranslationUseCase setTranslationFavoriteUseCase,
                             GetRefreshedTranslationUseCase getRefreshedTranslationUseCase,
                             GetLanguagesFromTranslationUseCase getLanguagesFromTranslationUseCase,
                             INetworkManager manager) {
    if (translateUseCase == null || getLastTranslationUseCase == null
        || manager == null || setTranslationFavoriteUseCase == null
        || getRefreshedTranslationUseCase == null || getLanguagesFromTranslationUseCase == null) {
      throw new IllegalArgumentException("All presenter arguments must be non null");
    }

    mTranslateUseCase = translateUseCase;
    mGetLastTranslationUseCase = getLastTranslationUseCase;
    mSaveTranslationUseCase = setTranslationFavoriteUseCase;
    mGetRefreshedTranslationUseCase = getRefreshedTranslationUseCase;
    mGetLanguagesFromTranslationUseCase = getLanguagesFromTranslationUseCase;
    mNetworkManager = manager;
  }

  // ----------------------------------- Presenter lifecycle --------------------------------------

  @Override
  protected void onViewAttached() {
    if (mCachedTranslation != null) {
      getView().setTranslation(mCachedTranslation);
      mCachedTranslation = null;
    } else if (mCachedSetFavoriteToggleToFalse) {
      mCachedSetFavoriteToggleToFalse = false;
      getView().setToggleFavoriteValue(false);
    }

    if (mCachedLanguages != null) {
      getView().setLanguages(mCachedLanguages);
      mCachedLanguages = null;
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
    mCachedLanguages = getView().getLanguagesIfInitialized();
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

  // -------------------------- GetRefreshedTranslationUseCase callbacks --------------------------

  @Override
  public void onGetRefreshedTranslationResult(Translation translation) {
    if (getView() != null) {
      getView().setToggleFavoriteValue(translation.ifFavorite());
    } else {
      mCachedTranslation = translation;
    }
  }

  @Override
  public void onGetRefreshedTranslationException(ExceptionBundle exceptionBundle) {
    if (exceptionBundle.getReason() == ExceptionBundle.Reason.NULL_POINTER) {
      if (getView() != null) {
        getView().setToggleFavoriteValue(false);
      } else {
        mCachedSetFavoriteToggleToFalse = true;
      }
    }
  }

  // ----------------------- GetLanguagesFromTranslationUseCase callbacks -------------------------

  @Override
  public void onGetLanguagesFromTranslationSuccess(Pair<Translation, LanguagePair> result) {
    if (getView() != null) {
      getView().setTranslation(result.first);
      getView().setLanguages(result.second);
    } else {
      mCachedTranslation = result.first;
      mCachedLanguages = result.second;
    }
  }

  @Override
  public void onGetLanguagesFromTranslationException(ExceptionBundle exception) {

  }

  // ---------------------------- GetLastTranslationUseCase callbacks -----------------------------

  @Override
  public void onGetLastTranslationSuccess(Pair<Translation, LanguagePair> result) {
    if (getView() != null) {
      getView().setLanguages(result.second);
      getView().setTranslation(result.first);
      getView().showImageClear();
      getView().setSwapLanguagesListener();
    }
  }

  @Override
  public void onGetLastTranslationException(Pair<ExceptionBundle, LanguagePair> result) {
    if (getView() != null) {
      getView().setLanguages(result.second);
      getView().hideImageClear();
      getView().setTextWatcher();
      getView().setSwapLanguagesListener();
    }
  }

  // -------------------------------- TranslateUseCase callbacks ----------------------------------


  @Override
  public void onSaveTranslationAfterGettingSuccess() {
    if (getView() != null) {
      getView().onTranslationSaved();
    } else {
      mCachedTranslationSaved = true;
    }
  }

  @Override
  public void onSaveTranslationAfterGettingException(ExceptionBundle exceptionBundle) {
    // TODO
  }

  @Override
  public void onTranslateSuccess(Translation translation) {
    disposeTranslationSubscription();

    if (getView() != null) {
      getView().hideProgressBar();
      getView().hideNoInternet();
      getView().setTranslation(translation);
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
            break;
        }
        if (errorMessage != null) {
          getView().showAlertDialog(errorMessage);
        }
      }
    }
  }

  // ----------------------------- SaveTranslationUseCase callbacks -------------------------------

  @Override
  public void onSaveTranslationSuccess() {
    if (getView() != null) {
      getView().onTranslationFavoriteChanged();
    } else {
      mCachedTranslationFavoriteChanged = true;
    }
  }

  @Override
  public void onSaveTranslationException(ExceptionBundle exceptionBundle) {
    // TODO
  }

  // ------------------------------------- public methods -----------------------------------------

  public void onRefreshFavoriteValue(String originalText, String translatedText,
                                     String languagePairText, boolean favorite) {
    Translation translation = new Translation(
        originalText,
        translatedText,
        languagePairText,
        false);

    mGetRefreshedTranslationUseCase.run(this, translation);
  }

  public void onSetTranslationData(Translation translation) {
    mGetLanguagesFromTranslationUseCase.run(this, translation);
  }

  public void onFirstStart() {
    mGetLastTranslationUseCase.run(this);
  }

  public void onNotFirstStart() {
    if (getView() != null) {
      getView().setTextWatcher();
      getView().setSwapLanguagesListener();
      getView().setToggleFavoriteListener();
    }
  }

  public void onToggleCheckedChanged(String originalText, String translatedText,
                                     String languageCodePair, boolean favorite) {
    Translation translation = new Translation(originalText, translatedText, languageCodePair, favorite);
    mSaveTranslationUseCase.run(this, translation);
  }

  public void onInputTextClear() {
    mTranslateUseCase.cancel();
    disposeTranslationSubscription();

    if (getView() != null) {
      getView().hideProgressBar();
      getView().hideNoInternet();
      getView().hideTranslationLayout();
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
      mTranslateUseCase.run(this, originalText, mCachedLanguages.getLanguageCodePair());
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
    private final SaveTranslationUseCase setTranslationFavoriteUseCase;
    private final GetRefreshedTranslationUseCase getRefreshedTranslationUseCase;
    private final GetLanguagesFromTranslationUseCase getLanguagesFromTranslationUseCase;
    private final INetworkManager networkManager;


    public Factory(TranslateUseCase translateUseCase,
                   GetLastTranslationUseCase getLastTranslationUseCase,
                   SaveTranslationUseCase setTranslationFavoriteUseCase,
                   GetRefreshedTranslationUseCase getRefreshedTranslationUseCase,
                   GetLanguagesFromTranslationUseCase getLanguagesFromTranslationUseCase,
                   INetworkManager networkManager) {
      this.translateUseCase = translateUseCase;
      this.getLastTranslationUseCase = getLastTranslationUseCase;
      this.setTranslationFavoriteUseCase = setTranslationFavoriteUseCase;
      this.getRefreshedTranslationUseCase = getRefreshedTranslationUseCase;
      this.getLanguagesFromTranslationUseCase = getLanguagesFromTranslationUseCase;
      this.networkManager = networkManager;
    }

    @Override
    public TranslatePresenter create() {
      return new TranslatePresenter(translateUseCase,
          getLastTranslationUseCase,
          setTranslationFavoriteUseCase,
          getRefreshedTranslationUseCase,
          getLanguagesFromTranslationUseCase,
          networkManager);
    }
  }
}
