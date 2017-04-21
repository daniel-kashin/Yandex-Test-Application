package com.danielkashin.yandextestapplication.presentation_layer.presenter.history;

import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.DeleteTranslationsUseCase;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.GetTranslationsUseCase;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.RefreshTranslationUseCase;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.IPresenterFactory;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.Presenter;
import com.danielkashin.yandextestapplication.presentation_layer.view.history.IHistoryView;

import java.util.ArrayList;


public class HistoryPresenter extends Presenter<IHistoryView>
    implements GetTranslationsUseCase.Callbacks, DeleteTranslationsUseCase.Callbacks,
    RefreshTranslationUseCase.Callbacks {

  private static final int TRANSLATIONS_PER_UPLOAD = 50;

  private final GetTranslationsUseCase mGetTranslationsUseCase;
  private final DeleteTranslationsUseCase mDeleteTranslationsUseCase;
  private final RefreshTranslationUseCase mRefreshTranslationUseCase;

  private ArrayList<Translation> mCachedTranslations;
  private boolean mCachedTranslationsClearBeforeAdd;
  private boolean mCachedOnDeleteTranslationsSuccess;
  private boolean mCachedOnTranslationRefreshedSuccess;


  private HistoryPresenter(GetTranslationsUseCase getTranslationsUseCase,
                           DeleteTranslationsUseCase deleteTranslationsUseCase,
                           RefreshTranslationUseCase refreshTranslationUseCase) {
    if (getTranslationsUseCase == null || deleteTranslationsUseCase == null
        || refreshTranslationUseCase == null) {
      throw new IllegalArgumentException("All presenter arguments must be non null");
    }

    mGetTranslationsUseCase = getTranslationsUseCase;
    mDeleteTranslationsUseCase = deleteTranslationsUseCase;
    mRefreshTranslationUseCase = refreshTranslationUseCase;
  }

  // ----------------------------------- Presenter lifecycle --------------------------------------

  @Override
  protected void onViewDetached() {
    getView().removeAdapterCallbacks();
  }

  @Override
  protected void onViewAttached() {
    if (mCachedTranslations != null) {
      getView().showNotEmptyContentInterface();
      getView().addTranslationsToAdapter(mCachedTranslations, mCachedTranslationsClearBeforeAdd);
      mCachedTranslations = null;
    }

    if (mCachedOnDeleteTranslationsSuccess) {
      mCachedOnDeleteTranslationsSuccess = false;
      getView().onDeleteTranslationsSuccess();
    }

    if (mCachedOnTranslationRefreshedSuccess) {
      mCachedOnTranslationRefreshedSuccess = false;
      getView().onTranslationRefreshedSuccess();
    }
  }

  @Override
  protected void onDestroyed() {
    mGetTranslationsUseCase.cancel();
  }

  // --------------------------- RefreshTranslationUseCase callbacks ------------------------------

  @Override
  public void onRefreshTranslationSuccess() {
    if (getView() != null) {
      getView().onTranslationRefreshedSuccess();
    } else {
      mCachedOnTranslationRefreshedSuccess = true;
    }
  }

  @Override
  public void onRefreshTranslationException(ExceptionBundle exceptionBundle) {
    // TODO
  }

  // --------------------------- DeleteTranslationsUseCase callbacks ------------------------------

  @Override
  public void onDeleteTranslationsSuccess() {
    if (getView() != null) {
      getView().onDeleteTranslationsSuccess();
    } else {
      mCachedOnDeleteTranslationsSuccess = true;
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
      getView().setAdapterCallbacks();
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
          getView().removeAdapterCallbacks();

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

  public void onToggleClicked(Translation translation) {
    mRefreshTranslationUseCase.run(this, translation);
  }

  public void deleteTranslations() {
    mDeleteTranslationsUseCase.run(this);
  }

  public void refreshTranslations(int translationCount, String searchRequest) {
    if (TRANSLATIONS_PER_UPLOAD > translationCount) {
      translationCount = TRANSLATIONS_PER_UPLOAD;
    }

    mGetTranslationsUseCase.run(this, 0, translationCount, searchRequest);
  }

  // ------------------------------------- Inner classes -----------------------------------------

  public static final class Factory
      implements IPresenterFactory<HistoryPresenter, IHistoryView> {

    private final GetTranslationsUseCase getTranslationsUseCase;
    private final DeleteTranslationsUseCase deleteTranslationsUseCase;
    private final RefreshTranslationUseCase refreshTranslationUseCase;


    public Factory(GetTranslationsUseCase getTranslationsUseCase,
                   DeleteTranslationsUseCase deleteTranslationsUseCase,
                   RefreshTranslationUseCase refreshTranslationUseCase) {
      this.getTranslationsUseCase = getTranslationsUseCase;
      this.deleteTranslationsUseCase = deleteTranslationsUseCase;
      this.refreshTranslationUseCase = refreshTranslationUseCase;
    }

    @Override
    public HistoryPresenter create() {
      return new HistoryPresenter(getTranslationsUseCase, deleteTranslationsUseCase, refreshTranslationUseCase);
    }
  }
}