package com.danielkashin.yandextestapplication.presentation_layer.presenter.pick_language;


import com.danielkashin.yandextestapplication.data_layer.managers.network.INetworkManager;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Language;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.GetAllSupportedLanguagesUseCase;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.GetLanguagesFromTranslationUseCase;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.GetLastTranslationUseCase;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.GetRefreshedTranslationUseCase;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.SaveTranslationUseCase;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.TranslateUseCase;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.IPresenterFactory;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.Presenter;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.translate.TranslatePresenter;
import com.danielkashin.yandextestapplication.presentation_layer.view.pick_language.IPickLanguageView;
import com.danielkashin.yandextestapplication.presentation_layer.view.translate.ITranslateView;

import java.util.ArrayList;

public class PickLanguagePresenter extends Presenter<IPickLanguageView>
    implements IPickLanguagePresenter, GetAllSupportedLanguagesUseCase.Callbacks {

  private final GetAllSupportedLanguagesUseCase mGetAllSupportedLanguagesUseCase;
  private ArrayList<Language> mCachedLanguages;


  private PickLanguagePresenter(GetAllSupportedLanguagesUseCase getAllSupportedLanguagesUseCase) {
    if (getAllSupportedLanguagesUseCase == null) {
      throw new IllegalArgumentException("All presenter arguments must be non null");
    }

    mGetAllSupportedLanguagesUseCase = getAllSupportedLanguagesUseCase;
  }

  // --------------------------------------- lifecycle --------------------------------------------

  @Override
  protected void onViewDetached() {
    getView().removeAdapterCallbacks();
  }

  @Override
  protected void onViewAttached() {
    if (mCachedLanguages != null) {
      getView().setAdapterCallbacks();
      getView().addLanguagesToAdapter(mCachedLanguages);
      mCachedLanguages = null;
    }
  }

  @Override
  protected void onDestroyed() {

  }

  // ------------------------ GetAllSupportedLanguagesUseCase.Callbacks ---------------------------

  @Override
  public void onGetLanguagesFromTranslationSuccess(ArrayList<Language> result) {
    if (getView() != null) {
      getView().setAdapterCallbacks();
      getView().addLanguagesToAdapter(result);
    } else {
      mCachedLanguages = result;
    }
  }

  // -------------------------------- IPickLanguagesPresenter -------------------------------------

  @Override
  public void onFirstStart() {
    mGetAllSupportedLanguagesUseCase.run(this);
  }

  // --------------------------------------- inner types --------------------------------------------

  public static final class Factory
      implements IPresenterFactory<PickLanguagePresenter, IPickLanguageView> {

    private GetAllSupportedLanguagesUseCase getAllSupportedLanguagesUseCase;

    public Factory(GetAllSupportedLanguagesUseCase getAllSupportedLanguagesUseCase) {
      this.getAllSupportedLanguagesUseCase = getAllSupportedLanguagesUseCase;
    }

    @Override
    public PickLanguagePresenter create() {
      return new PickLanguagePresenter(getAllSupportedLanguagesUseCase);
    }
  }
}
