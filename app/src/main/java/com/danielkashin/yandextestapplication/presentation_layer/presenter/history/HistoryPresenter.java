package com.danielkashin.yandextestapplication.presentation_layer.presenter.history;

import android.support.annotation.NonNull;

import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.GetTranslationsUseCase;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.IPresenterFactory;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.Presenter;
import com.danielkashin.yandextestapplication.presentation_layer.view.history.IHistoryView;

import java.util.ArrayList;
import java.util.List;


public class HistoryPresenter extends Presenter<IHistoryView>
    implements GetTranslationsUseCase.Callbacks {

  private static final int TRANSLATIONS_PER_UPLOAD = 50;

  @NonNull
  private final GetTranslationsUseCase mGetTranslationsUseCase;

  private ArrayList<Translation> mCachedTranslations;
  private boolean mCachedTranslationsClearBeforeAdd;


  public HistoryPresenter(@NonNull GetTranslationsUseCase getTranslationsUseCase) {
    mGetTranslationsUseCase = getTranslationsUseCase;
  }

  // ----------------------------------- Presenter lifecycle --------------------------------------

  @Override
  protected void onViewDetached() {

  }

  @Override
  protected void onViewAttached() {
    if (mCachedTranslations != null) {
      getView().showNotEmptyContentInterface();
      getView().addTranslationsToAdapter(mCachedTranslations, mCachedTranslationsClearBeforeAdd);

      mCachedTranslations = null;
    }
  }

  @Override
  protected void onDestroyed() {
    mGetTranslationsUseCase.cancel();
  }

  // ----------------------------- GetTranslationUseCase callbacks -------------------------------

  @Override
  public void onGetTranslationsSuccess(ArrayList<Translation> translations, final int offset,
                                       final String searchRequest) {
    if (getView() != null) {
      getView().showNotEmptyContentInterface();
      getView().addTranslationsToAdapter(translations, offset == 0);
    } else {
      mCachedTranslations = translations;
      mCachedTranslationsClearBeforeAdd = offset == 0;
    }
  }

  @Override
  public void onGetTranslationsException(ExceptionBundle exceptionBundle, int offset,
                                         final String searchRequest) {
    switch (exceptionBundle.getReason()) {
      case EMPTY_TRANSLATIONS:
        if (offset == 0) {
          if (getView() != null) {
            getView().clearTranslationAdapter();
            if (searchRequest == null) {
              getView().showEmptyContentInterface();
            } else {
              getView().showEmptySearchContentInterface();
            }
          }
        }
        break;
    }
  }

  // ---------------------------------- public methods --------------------------------------------

  public void refreshTranslations(String searchRequest) {
    mGetTranslationsUseCase.run(this, 0, TRANSLATIONS_PER_UPLOAD, searchRequest);
  }

  // ------------------------------------- Inner classes -----------------------------------------

  public static final class Factory
      implements IPresenterFactory<HistoryPresenter, IHistoryView> {

    @NonNull
    private final GetTranslationsUseCase getTranslationsUseCase;

    public Factory(@NonNull GetTranslationsUseCase getTranslationsUseCase) {
      this.getTranslationsUseCase = getTranslationsUseCase;
    }

    @Override
    public HistoryPresenter create() {
      return new HistoryPresenter(getTranslationsUseCase);
    }
  }

}
