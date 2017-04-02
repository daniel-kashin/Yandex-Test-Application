package com.danielkashin.yandextestapplication.presentation_layer.presenter.translate;


import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;

import com.danielkashin.yandextestapplication.R;
import com.danielkashin.yandextestapplication.data_layer.entities.local.DatabaseTranslation;
import com.danielkashin.yandextestapplication.data_layer.entities.remote.NetworkTranslation;
import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.data_layer.services.local.ITranslateDatabaseService;
import com.danielkashin.yandextestapplication.data_layer.services.local.TranslateDatabaseService;
import com.danielkashin.yandextestapplication.data_layer.services.remote.ITranslateNetworkService;
import com.danielkashin.yandextestapplication.data_layer.services.remote.TranslateNetworkService;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.local.GetLastTranslationUseCase;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.local.SaveTranslationUseCase;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.remote.TranslateUseCase;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.IPresenterFactory;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.Presenter;
import com.danielkashin.yandextestapplication.presentation_layer.view.translate.ITranslateView;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class TranslatePresenter extends Presenter<ITranslateView>
    implements TranslateUseCase.Callbacks, SaveTranslationUseCase.Callbacks, GetLastTranslationUseCase.Callbacks {

  private final TranslateUseCase mTranslateUseCase;
  private final SaveTranslationUseCase mSaveTranslationUseCase;
  private final GetLastTranslationUseCase mGetLastTranslationUseCase;
  private String mLangFrom;
  private String mLangTo;
  private boolean mLastTranslationInitialized;
  private TextWatcher mTextWatcher;


  public TranslatePresenter(
      TranslateUseCase translateUseCase,
      SaveTranslationUseCase saveTranslationUseCase,
      GetLastTranslationUseCase getLastTranslationUseCase){

    mTranslateUseCase = translateUseCase;
    mSaveTranslationUseCase = saveTranslationUseCase;
    mGetLastTranslationUseCase = getLastTranslationUseCase;
  }


  public void onInputTextClear() {
    if (getView() != null) {
      getView().hideProgressBar();
      getView().setTranslatedText("");
    }
    mTranslateUseCase.cancel();
  }

  public void onInputTextChanged(String text) {
    getView().showProgressBar();
    mTranslateUseCase.cancel();
    mTranslateUseCase.run(this, text, "ru-en");
  }

  // ----------------------------------- Presenter lifecycle --------------------------------------

  @Override
  protected void onViewAttached() {
    if (getView() != null && mTranslateUseCase.isRunning()) {
      getView().showProgressBar();
    }

    if (!mLastTranslationInitialized){
      mGetLastTranslationUseCase.run(this);
      mLastTranslationInitialized = true;
    }

    if (mTextWatcher == null) {
      getView().setInputTextListener(getTextWatcher());
    }
  }

  @Override
  protected void onViewDetached() {
    if (mTextWatcher != null) {
      getView().removeInputTextListener(getTextWatcher());
      mTextWatcher = null;
    }
  }

  @Override
  protected void onDestroyed() {
    mTranslateUseCase.cancel();
    mGetLastTranslationUseCase.cancel();
  }

  // ----------------------------- SaveTranslationUseCase callbacks -------------------------------

  @Override
  public void onSaveTranslationError(ExceptionBundle exception) {

  }

  // ---------------------------- GetLastTranslationUseCase callbacks -----------------------------

  @Override
  public void onGetLastTranslationResult(DatabaseTranslation translation) {
    if (getView() != null) {
      getView().removeInputTextListener(getTextWatcher());
      mTextWatcher = null;

      getView().setInputText(translation.getOriginalText());

      getView().setInputTextListener(getTextWatcher());

      getView().setTranslatedText(translation.getTranslatedText());
    }
  }

  @Override
  public void onGetLastTranslationError(ExceptionBundle exception) {

  }

  // -------------------------------- TranslateUseCase callbacks ----------------------------------

  @Override
  public void onTranslateSuccess(Pair<String, NetworkTranslation> result) {
    if (getView() != null) {
      mSaveTranslationUseCase.run(this, result.first, result.second.getText(), result.second.getLanguage());

      getView().hideProgressBar();
      getView().setTranslatedText(result.second.getText());
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

  // ----------------------------------- private methods -----------------------------------------

  private TextWatcher getTextWatcher(){
    if (mTextWatcher == null) {
      mTextWatcher =  new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
          if (editable.toString().replace(" ", "").isEmpty()) {
            onInputTextClear();
          } else {
            onInputTextChanged(editable.toString());
          }
        }
      };
    }

    return mTextWatcher;
  }

  // ------------------------------------- Inner classes -----------------------------------------

  public static final class Factory
      implements IPresenterFactory<TranslatePresenter, ITranslateView> {

    @Override
    public TranslatePresenter create() {
      // bind network service
      OkHttpClient okHttpClient = new OkHttpClient.Builder()
          .readTimeout(10, TimeUnit.SECONDS)
          .connectTimeout(10, TimeUnit.SECONDS)
          .build();
      ITranslateNetworkService networkService = TranslateNetworkService
          .Factory
          .create(okHttpClient);

      // bind database service
      ITranslateDatabaseService databaseService = TranslateDatabaseService
          .Factory
          .create();

      // bind useCases
      TranslateUseCase translateUseCase = new TranslateUseCase(AsyncTask.THREAD_POOL_EXECUTOR, networkService);
      SaveTranslationUseCase saveTranslationUseCase = new SaveTranslationUseCase(databaseService);
      GetLastTranslationUseCase getLastTranslationUseCase = new GetLastTranslationUseCase(databaseService);

      return new TranslatePresenter(translateUseCase, saveTranslationUseCase, getLastTranslationUseCase);
    }

  }

}
