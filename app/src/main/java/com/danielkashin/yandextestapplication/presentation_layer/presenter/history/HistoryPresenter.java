package com.danielkashin.yandextestapplication.presentation_layer.presenter.history;

import com.danielkashin.yandextestapplication.R;
import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.DeleteTranslationUseCase;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.DeleteTranslationsUseCase;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.GetTranslationsUseCase;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.RefreshTranslationUseCase;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.IPresenterFactory;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.Presenter;
import com.danielkashin.yandextestapplication.presentation_layer.view.history.IHistoryView;

import java.util.ArrayList;


public class HistoryPresenter extends Presenter<IHistoryView> implements IHistoryPresenter,
    GetTranslationsUseCase.Callbacks, DeleteTranslationsUseCase.Callbacks,
    RefreshTranslationUseCase.Callbacks, DeleteTranslationUseCase.Callbacks {

  // count of translations uploading at once
  private static final int TRANSLATIONS_PER_UPLOAD = 50;

  // use cases
  private final GetTranslationsUseCase mGetTranslationsUseCase;
  private final DeleteTranslationsUseCase mDeleteTranslationsUseCase;
  private final RefreshTranslationUseCase mRefreshTranslationUseCase;
  private final DeleteTranslationUseCase mDeleteTranslationUseCase;

  // when view is not attached to the presenter, callbacks can be triggered,
  // so it is needed to cache results to restore them if view will be attached later
  private ArrayList<Translation> mCachedTranslations;
  private Translation mCachedTranslationDeleted;
  private boolean mCachedTranslationsClearBeforeAdd;
  private boolean mCachedOnDeleteTranslationsSuccess;
  private boolean mCachedOnTranslationRefreshedSuccess;
  private boolean mCachedEmptyContentInterface;
  private boolean mCachedEmptySearchContentInterface;
  private boolean mCachedSetAdapterEndReached;


  private HistoryPresenter(GetTranslationsUseCase getTranslationsUseCase,
                           DeleteTranslationsUseCase deleteTranslationsUseCase,
                           RefreshTranslationUseCase refreshTranslationUseCase,
                           DeleteTranslationUseCase deleteTranslationUseCase) {
    if (getTranslationsUseCase == null || deleteTranslationsUseCase == null
        || refreshTranslationUseCase == null || deleteTranslationUseCase == null) {
      throw new IllegalArgumentException("All presenter arguments must be non null");
    }

    mGetTranslationsUseCase = getTranslationsUseCase;
    mDeleteTranslationsUseCase = deleteTranslationsUseCase;
    mRefreshTranslationUseCase = refreshTranslationUseCase;
    mDeleteTranslationUseCase = deleteTranslationUseCase;
  }

  // ----------------------------------- Presenter lifecycle --------------------------------------

  @Override
  protected void onViewDetached() {
    getView().removeAdapterCallbacks();
    getView().removeRecyclerViewScrollListener();
  }

  @Override
  protected void onViewAttached() {
    // restore cached results

    if (mCachedTranslations != null) {
      getView().showNotEmptyContentInterface();
      getView().addTranslationsToAdapter(mCachedTranslations, mCachedTranslationsClearBeforeAdd);
      mCachedTranslations = null;
    }

    if (mCachedEmptySearchContentInterface || mCachedEmptyContentInterface) {
      getView().clearAdapter();
      getView().removeAdapterCallbacks();
      getView().removeRecyclerViewScrollListener();
      if (mCachedEmptyContentInterface) {
        getView().showEmptyContentInterface();
      } else {
        getView().showEmptySearchContentInterface();
      }

      mCachedEmptyContentInterface = false;
      mCachedEmptySearchContentInterface = false;
    }

    if (mCachedSetAdapterEndReached) {
      getView().setAdapterEndReached();
      getView().removeRecyclerViewScrollListener();
    }

    if (mCachedOnDeleteTranslationsSuccess) {
      getView().onDeleteTranslationsSuccess();
      mCachedOnDeleteTranslationsSuccess = false;
    }

    if (mCachedTranslationDeleted != null) {
      getView().onDeleteTranslationSuccess(mCachedTranslationDeleted);
      mCachedTranslationDeleted = null;
    }

    if (mCachedOnTranslationRefreshedSuccess) {
      getView().onTranslationRefreshedSuccess();
      mCachedOnTranslationRefreshedSuccess = false;
    }
  }

  @Override
  protected void onDestroyed() {
    mGetTranslationsUseCase.cancel();
  }

  // ---------------------------- DeleteTranslationUseCase callbacks ------------------------------

  @Override
  public void onDeleteTranslationSuccess(Translation translation) {
    if (getView() != null) {
      getView().onDeleteTranslationSuccess(translation);
    } else {
      mCachedTranslationDeleted = translation;
    }
  }

  @Override
  public void onDeleteTranslationException(ExceptionBundle exception) {
    if (exception.getReason() == ExceptionBundle.Reason.DELETE_DENIED) {
      if (getView() != null) {
        getView().showAlertDialog(getView().getStringById(R.string.delete_denied));
      }
    }
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
    if (exception.getReason() == ExceptionBundle.Reason.DELETE_SOURCE_IS_EMPTY) {
      if (getView() != null) {
        getView().showDeleteTranslationsSourceIsEmpty();
      }
    } else if (exception.getReason() == ExceptionBundle.Reason.DELETE_DENIED) {
      if (getView() != null) {
        getView().showAlertDialog(getView().getStringById(R.string.delete_denied));
      }
    }
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
    // do nothing
  }

  // ----------------------------- GetTranslationUseCase callbacks -------------------------------

  @Override
  public void onGetTranslationsSuccess(ArrayList<Translation> translations, final int offset,
                                       final String searchRequest) {
    if (getView() != null) {
      getView().showNotEmptyContentInterface();
      getView().addTranslationsToAdapter(translations, offset == 0);
      getView().setAdapterCallbacks();
      getView().setRecyclerViewScrollListener();
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
            getView().clearAdapter();
            getView().removeAdapterCallbacks();
            getView().removeRecyclerViewScrollListener();
            if (searchRequest == null || searchRequest.isEmpty()) {
              getView().showEmptyContentInterface();
            } else {
              getView().showEmptySearchContentInterface();
            }
          } else {
            if (searchRequest == null || searchRequest.isEmpty()) {
              mCachedEmptyContentInterface = true;
            } else {
              mCachedEmptySearchContentInterface = true;
            }
          }
        } else {
          if (getView() != null) {
            getView().setAdapterEndReached();
            getView().removeRecyclerViewScrollListener();
          } else {
            mCachedSetAdapterEndReached = true;
          }
        }
        break;
    }
  }

  // ------------------------------------ IHistoryPresenter ----------------------------------------

  @Override
  public void onAdapterItemLongClicked(Translation translation) {
    mDeleteTranslationUseCase.run(this, translation);
  }

  @Override
  public void onAdapterToggleClicked(Translation translation) {
    mRefreshTranslationUseCase.run(this, translation);
  }

  @Override
  public void onDeleteTranslations() {
    mDeleteTranslationsUseCase.run(this);
  }

  @Override
  public void onUploadTranslations(int offset, String searchRequest) {
    mGetTranslationsUseCase.cancel();
    mGetTranslationsUseCase.run(this, offset, TRANSLATIONS_PER_UPLOAD, searchRequest);
  }

  // ------------------------------------- Inner classes -----------------------------------------

  public static final class Factory
      implements IPresenterFactory<HistoryPresenter, IHistoryView> {

    private final GetTranslationsUseCase getTranslationsUseCase;
    private final DeleteTranslationsUseCase deleteTranslationsUseCase;
    private final RefreshTranslationUseCase refreshTranslationUseCase;
    private final DeleteTranslationUseCase deleteTranslationUseCase;


    public Factory(GetTranslationsUseCase getTranslationsUseCase,
                   DeleteTranslationsUseCase deleteTranslationsUseCase,
                   RefreshTranslationUseCase refreshTranslationUseCase,
                   DeleteTranslationUseCase deleteTranslationUseCase) {
      this.getTranslationsUseCase = getTranslationsUseCase;
      this.deleteTranslationsUseCase = deleteTranslationsUseCase;
      this.refreshTranslationUseCase = refreshTranslationUseCase;
      this.deleteTranslationUseCase = deleteTranslationUseCase;
    }

    @Override
    public HistoryPresenter create() {
      return new HistoryPresenter(getTranslationsUseCase, deleteTranslationsUseCase,
          refreshTranslationUseCase, deleteTranslationUseCase);
    }
  }
}