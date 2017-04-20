package com.danielkashin.yandextestapplication.presentation_layer.presenter.history;

import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.DeleteTranslationsUseCase;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.GetTranslationsUseCase;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.IPresenterFactory;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.Presenter;
import com.danielkashin.yandextestapplication.presentation_layer.view.history.IHistoryView;

import java.util.ArrayList;


public class HistoryPresenter extends Presenter<IHistoryView>
    implements GetTranslationsUseCase.Callbacks, DeleteTranslationsUseCase.Callbacks {

  private static final int TRANSLATIONS_PER_UPLOAD = 50;

  private final GetTranslationsUseCase mGetTranslationsUseCase;
  private final DeleteTranslationsUseCase mDeleteTranslationsUseCase;

  private ArrayList<Translation> mCachedTranslations;
  private boolean mCachedTranslationsClearBeforeAdd;
  private boolean mCachedOnDataChanged;


  private HistoryPresenter(GetTranslationsUseCase getTranslationsUseCase,
                          DeleteTranslationsUseCase deleteTranslationsUseCase) {
    if (getTranslationsUseCase == null || deleteTranslationsUseCase == null) {
      throw new IllegalArgumentException("All presenter arguments must be non null");
    }

    mGetTranslationsUseCase = getTranslationsUseCase;
    mDeleteTranslationsUseCase = deleteTranslationsUseCase;
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

    if (mCachedOnDataChanged) {
      getView().onDeleted();
      mCachedOnDataChanged = false;
    }
  }

  @Override
  protected void onDestroyed() {
    mGetTranslationsUseCase.cancel();
  }

  // --------------------------- DeleteTranslationsUseCase callbacks ------------------------------

  @Override
  public void onDeleteTranslationsSuccess() {
    if (getView() != null) {
      getView().onDeleted();
    } else {
      mCachedOnDataChanged = true;
    }
  }

  @Override
  public void onDeleteTranslationsException(ExceptionBundle exception) {
    // TODO
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

  public void deleteTranslations() {
    mDeleteTranslationsUseCase.run(this);
  }

  public void refreshTranslations(String searchRequest) {
    mGetTranslationsUseCase.run(this, 0, TRANSLATIONS_PER_UPLOAD, searchRequest);
  }

  // ------------------------------------- Inner classes -----------------------------------------

  public static final class Factory
      implements IPresenterFactory<HistoryPresenter, IHistoryView> {

    private final GetTranslationsUseCase getTranslationsUseCase;
    private final DeleteTranslationsUseCase deleteTranslationsUseCase;


    public Factory(GetTranslationsUseCase getTranslationsUseCase,
                   DeleteTranslationsUseCase deleteTranslationsUseCase) {
      this.getTranslationsUseCase = getTranslationsUseCase;
      this.deleteTranslationsUseCase = deleteTranslationsUseCase;
    }

    @Override
    public HistoryPresenter create() {
      return new HistoryPresenter(getTranslationsUseCase, deleteTranslationsUseCase);
    }
  }
}
