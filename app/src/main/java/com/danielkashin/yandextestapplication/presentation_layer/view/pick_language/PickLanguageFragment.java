package com.danielkashin.yandextestapplication.presentation_layer.view.pick_language;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.danielkashin.yandextestapplication.R;
import com.danielkashin.yandextestapplication.data_layer.repository.languages.ISupportedLanguagesRepository;
import com.danielkashin.yandextestapplication.data_layer.repository.languages.SupportedLanguagesRepository;
import com.danielkashin.yandextestapplication.data_layer.services.supported_languages.local.ISupportedLanguagesLocalService;
import com.danielkashin.yandextestapplication.data_layer.services.supported_languages.local.SupportedLanguagesLocalService;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Language;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.GetAllSupportedLanguagesUseCase;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.languages.ILanguagesAdapter;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.languages.LanguagesAdapter;
import com.danielkashin.yandextestapplication.presentation_layer.custom_views.DividerItemDecoration;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.IPresenterFactory;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.pick_language.IPickLanguagePresenter;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.pick_language.PickLanguagePresenter;
import com.danielkashin.yandextestapplication.presentation_layer.view.base.PresenterFragment;
import com.danielkashin.yandextestapplication.presentation_layer.view.pick_language_holder.IPickLanguageHolderView;

import java.util.ArrayList;

public class PickLanguageFragment extends PresenterFragment<PickLanguagePresenter, IPickLanguageView>
    implements IPickLanguageView, ILanguagesAdapter.Callbacks {

  private RecyclerView mRecyclerView;

  private State mRestoredState;


  // ------------------------------------ lifecycle -----------------------------------------------

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (!(getActivity() instanceof IPickLanguageHolderView)) {
      throw new IllegalStateException("Parent activity of PickLanguageFragment must be an instance " +
          "of IPickLanguageHolderView");
    }

    mRestoredState = new State(savedInstanceState);

    if (!mRestoredState.isLanguagesAdapterInitialized()) {
      mRestoredState = new State(getArguments());
    }
  }

  @Override
  public void onStart() {
    super.onStart();

    if (!(getPresenter() instanceof IPickLanguagePresenter)) {
      throw new IllegalStateException("Presenter of PickLanguageFragment must be an instance" +
          " of IPickLanguagePresenter");
    }

    if (!mRestoredState.isLanguagesAdapterInitialized()) {
      ((IPickLanguagePresenter)getPresenter()).onFirstStart();
    } else {
      setAdapterCallbacks();
    }

  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    mRestoredState.setLanguagesAdapter((ILanguagesAdapter)mRecyclerView.getAdapter());
    mRestoredState.saveToOutState(outState);
  }

  // --------------------------- ILanguagesAdapter.Callbacks --------------------------------------

  @Override
  public void onItemClicked(Language language) {
    ((IPickLanguageHolderView)getActivity()).onLanguageChosen(language);
  }

  // -------------------------------- IPickLanguageView -------------------------------------------

  @Override
  public void setAdapterCallbacks() {
    ((ILanguagesAdapter)mRecyclerView.getAdapter()).addCallbacks(this);
  }

  @Override
  public void removeAdapterCallbacks() {
    ((ILanguagesAdapter)mRecyclerView.getAdapter()).removeCallbacks();
  }

  @Override
  public void addLanguagesToAdapter(ArrayList<Language> languages) {
    ((ILanguagesAdapter)mRecyclerView.getAdapter()).addLanguages(languages);
  }

  // --------------------------------- PresenterFragment ------------------------------------------

  @Override
  protected IPickLanguageView getViewInterface() {
    return this;
  }

  @Override
  protected IPresenterFactory<PickLanguagePresenter, IPickLanguageView> getPresenterFactory() {
    // bind services
    ISupportedLanguagesLocalService supportedLanguagesLocalService =
        SupportedLanguagesLocalService.Factory
            .create(getContext());

    // bind repositories
    ISupportedLanguagesRepository supportedLanguagesRepository =
        SupportedLanguagesRepository.Factory
            .create(supportedLanguagesLocalService);

    // bind use cases
    GetAllSupportedLanguagesUseCase getAllSupportedLanguagesUseCase =
        new GetAllSupportedLanguagesUseCase(supportedLanguagesRepository);


    return new PickLanguagePresenter.Factory(getAllSupportedLanguagesUseCase);
  }

  @Override
  protected int getFragmentId() {
    return PickLanguagePresenter.class.getSimpleName().hashCode();
  }

  @Override
  protected int getLayoutRes() {
    return R.layout.fragment_pick_language;
  }

  @Override
  protected void initializeView(View view) {
    mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
    mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), R.drawable.divider_light));
    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    if (mRestoredState.isLanguagesAdapterInitialized()) {
      mRecyclerView.setAdapter((RecyclerView.Adapter)mRestoredState.getLanguagesAdapter());
    } else {
      mRecyclerView.setAdapter(new LanguagesAdapter());
    }
  }

  // ------------------------------------- inner types --------------------------------------------

  private class State {

    private ILanguagesAdapter languagesAdapter;

    private State(Bundle bundle) {
      languagesAdapter = new LanguagesAdapter(bundle);
      if (!languagesAdapter.isInitialized()) {
        languagesAdapter = null;
      }
    }

    private void saveToOutState(Bundle outState){
      if (languagesAdapter != null) {
        languagesAdapter.onSaveInstanceState(outState);
      }
    }

    private void setLanguagesAdapter(ILanguagesAdapter languagesAdapter) {
      this.languagesAdapter = languagesAdapter;
    }

    private ILanguagesAdapter getLanguagesAdapter() {
      return languagesAdapter;
    }

    private boolean isLanguagesAdapterInitialized() {
      return languagesAdapter != null;
    }
  }

}
