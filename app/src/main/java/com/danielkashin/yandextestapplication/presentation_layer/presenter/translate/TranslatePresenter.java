package com.danielkashin.yandextestapplication.presentation_layer.presenter.translate;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import com.danielkashin.yandextestapplication.R;
import com.danielkashin.yandextestapplication.data_layer.entities.local.DatabaseTranslation;
import com.danielkashin.yandextestapplication.data_layer.entities.remote.NetworkTranslation;
import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.local.GetLastTranslationUseCase;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.remote.TranslateUseCase;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.IPresenterFactory;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.Presenter;
import com.danielkashin.yandextestapplication.presentation_layer.view.translate.ITranslateView;


public class TranslatePresenter extends Presenter<ITranslateView>
    implements TranslateUseCase.Callbacks, GetLastTranslationUseCase.Callbacks {

  private String mLangFrom;
  private String mLangTo;

  private final TranslateUseCase mTranslateUseCase;
  private final GetLastTranslationUseCase mGetLastTranslationUseCase;


  public TranslatePresenter(TranslateUseCase translateUseCase,
                            GetLastTranslationUseCase getLastTranslationUseCase) {

    mTranslateUseCase = translateUseCase;
    mGetLastTranslationUseCase = getLastTranslationUseCase;
  }


  public void onDataWasNotRestored() {
    // mGetLastTranslationUseCase.run(this);
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
  }

  @Override
  protected void onViewDetached() { }

  @Override
  protected void onDestroyed() {
    mTranslateUseCase.cancel();
    mGetLastTranslationUseCase.cancel();
  }

  // ---------------------------- GetLastTranslationUseCase callbacks -----------------------------

  @Override
  public void onGetLastTranslationResult(DatabaseTranslation translation) {
    if (getView() != null) {
      getView().setInputText(translation.getOriginalText());
      getView().setTranslatedText(translation.getTranslatedText());
      getView().setTextWatcher();
    }
  }

  @Override
  public void onGetLastTranslationError(ExceptionBundle exception) {

  }

  // -------------------------------- TranslateUseCase callbacks ----------------------------------

  @Override
  public void onTranslateSuccess(Translation translation) {
    if (getView() != null) {
      getView().hideProgressBar();
      getView().setTranslatedText(translation.getTranslatedText());
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

  public static final class Factory
      implements IPresenterFactory<TranslatePresenter, ITranslateView> {

    private final TranslateUseCase translateUseCase;
    private final GetLastTranslationUseCase getLastTranslationUseCase;


    public Factory(TranslateUseCase translateUseCase,
                   GetLastTranslationUseCase getLastTranslationUseCase){

      this.translateUseCase = translateUseCase;
      this.getLastTranslationUseCase = getLastTranslationUseCase;
    }

    @Override
    public TranslatePresenter create() {
      return new TranslatePresenter(translateUseCase, getLastTranslationUseCase);
    }

  }

}
