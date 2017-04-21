package com.danielkashin.yandextestapplication.presentation_layer.presenter.history;

import com.danielkashin.yandextestapplication.R;
import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.DeleteTranslationUseCase;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.DeleteTranslationsUseCase;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.GetTranslationsUseCase;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.SetTranslationDataUseCase;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.IPresenterFactory;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.Presenter;
import com.danielkashin.yandextestapplication.presentation_layer.view.history.IHistoryView;

import java.util.ArrayList;


public class HistoryPresenter extends Presenter<IHistoryView>
    implements GetTranslationsUseCase.Callbacks, DeleteTranslationsUseCase.Callbacks,
    SetTranslationDataUseCase.Callbacks, DeleteTranslationUseCase.Callbacks {

  private static final int TRANSLATIONS_PER_UPLOAD = 50;

  private final GetTranslationsUseCase mGetTranslationsUseCase;
  private final DeleteTranslationsUseCase mDeleteTranslationsUseCase;
  private final SetTranslationDataUseCase mSetTranslationDataUseCase;
  private final DeleteTranslationUseCase mDeleteTranslationUseCase;

  private ArrayList<Translation> mCachedTranslations;
  private boolean mCachedTranslationsClearBeforeAdd;
  private boolean mCachedOnDeleteSuccess;
  private boolean mCachedOnTranslationRefreshedSuccess;


  private HistoryPresenter(GetTranslationsUseCase getTranslationsUseCase,
                           DeleteTranslationsUseCase deleteTranslationsUseCase,
                           SetTranslationDataUseCase setTranslationDataUseCase,
                           DeleteTranslationUseCase deleteTranslationUseCase) {
    if (getTranslationsUseCase == null || deleteTranslationsUseCase == null
        || setTranslationDataUseCase == null || deleteTranslationUseCase == null) {
      throw new IllegalArgumentException("All presenter arguments must be non null");
    }

    mGetTranslationsUseCase = getTranslationsUseCase;
    mDeleteTranslationsUseCase = deleteTranslationsUseCase;
    mSetTranslationDataUseCase = setTranslationDataUseCase;
    mDeleteTranslationUseCase = deleteTranslationUseCase;
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

    if (mCachedOnDeleteSuccess) {
      mCachedOnDeleteSuccess = false;
      getView().onDeleteSuccess();
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

  // ---------------------------- DeleteTranslationUseCase callbacks ------------------------------

  @Override
  public void onDeleteTranslationSuccess() {
    if (getView() != null) {
      getView().onDeleteSuccess();
    } else {
      mCachedOnDeleteSuccess = true;
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
      getView().onDeleteSuccess();
    } else {
      mCachedOnDeleteSuccess = true;
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

  // --------------------------- SetTranslationDataUseCase callbacks ------------------------------

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

  public void onAdapterItemLongClicked(Translation translation) {
    mDeleteTranslationUseCase.run(this, translation);
  }

  public void onAdapterToggleClicked(Translation translation) {
    mSetTranslationDataUseCase.run(this, translation);
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
    private final SetTranslationDataUseCase setTranslationDataUseCase;
    private final DeleteTranslationUseCase deleteTranslationUseCase;


    public Factory(GetTranslationsUseCase getTranslationsUseCase,
                   DeleteTranslationsUseCase deleteTranslationsUseCase,
                   SetTranslationDataUseCase setTranslationDataUseCase,
                   DeleteTranslationUseCase deleteTranslationUseCase) {
      this.getTranslationsUseCase = getTranslationsUseCase;
      this.deleteTranslationsUseCase = deleteTranslationsUseCase;
      this.setTranslationDataUseCase = setTranslationDataUseCase;
      this.deleteTranslationUseCase = deleteTranslationUseCase;
    }

    @Override
    public HistoryPresenter create() {
      return new HistoryPresenter(getTranslationsUseCase, deleteTranslationsUseCase,
          setTranslationDataUseCase, deleteTranslationUseCase);
    }
  }
}