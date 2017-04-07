package com.danielkashin.yandextestapplication.presentation_layer.presenter.history;


import android.support.annotation.NonNull;

import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.GetTranslationsUseCase;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.IPresenterFactory;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.Presenter;
import com.danielkashin.yandextestapplication.presentation_layer.view.history.IHistoryView;

import java.util.List;

public class HistoryPresenter extends Presenter<IHistoryView>
    implements GetTranslationsUseCase.Callbacks {

  private static final int TRANSLATIONS_PER_UPLOAD = 50;

  private int mCurrentCount;

  @NonNull
  private final GetTranslationsUseCase mGetTranslationsUseCase;


  public HistoryPresenter(@NonNull GetTranslationsUseCase getTranslationsUseCase){
    mGetTranslationsUseCase = getTranslationsUseCase;
  }

  // ----------------------------------- Presenter lifecycle --------------------------------------

  @Override
  protected void onViewDetached() {

  }

  @Override
  protected void onViewAttached() {

  }

  @Override
  protected void onDestroyed() {
    mGetTranslationsUseCase.cancel();
  }

  // ----------------------------- GetTranslationUseCase callbacks -------------------------------

  @Override
  public void onGetTranslationsSuccess(List<Translation> translations) {
    if (getView() != null && translations.size() != 0){
      mCurrentCount = getView().getTranslationCount();
      getView().clearAdapter();
      getView().addTranslationsToAdapter(translations);
    }
  }

  @Override
  public void onGetTranslationsException(ExceptionBundle exceptionBundle) {

  }

  // ---------------------------------- public methods --------------------------------------------

  public void initializeAdapter(){
    mGetTranslationsUseCase.run(this, 0,
        mCurrentCount > TRANSLATIONS_PER_UPLOAD ? mCurrentCount : TRANSLATIONS_PER_UPLOAD);
  }

  // ------------------------------------- Inner classes -----------------------------------------

  public static final class Factory
      implements IPresenterFactory<HistoryPresenter, IHistoryView> {

    @NonNull
    private final GetTranslationsUseCase getTranslationsUseCase;

    public Factory(@NonNull GetTranslationsUseCase getTranslationsUseCase){
      this.getTranslationsUseCase = getTranslationsUseCase;
    }

    @Override
    public HistoryPresenter create() {
      return new HistoryPresenter(getTranslationsUseCase);
    }
  }

}
