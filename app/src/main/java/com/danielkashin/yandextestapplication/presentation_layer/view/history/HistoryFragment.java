package com.danielkashin.yandextestapplication.presentation_layer.view.history;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.danielkashin.yandextestapplication.R;
import com.danielkashin.yandextestapplication.data_layer.services.local.ITranslateLocalService;
import com.danielkashin.yandextestapplication.data_layer.services.remote.ITranslateRemoteService;
import com.danielkashin.yandextestapplication.data_layer.services.remote.TranslateRemoteService;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.danielkashin.yandextestapplication.domain_layer.repository.ITranslateRepository;
import com.danielkashin.yandextestapplication.domain_layer.repository.TranslateRepository;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.GetTranslationsUseCase;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.translations.ITranslationsModel;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.translations.TranslationsAdapter;
import com.danielkashin.yandextestapplication.presentation_layer.application.ITranslateLocalServiceProvider;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.IPresenterFactory;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.history.HistoryPresenter;
import com.danielkashin.yandextestapplication.presentation_layer.view.base.PresenterFragment;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class HistoryFragment extends PresenterFragment<HistoryPresenter, IHistoryView>
    implements IHistoryView {

  private static final String KEY_ONLY_FAVORITE = HistoryFragment.class.toString() + "KEY_ONLY_FAVORITE";
  private boolean mOnlyFavorite;
  private RecyclerViewState mRecyclerViewState;

  private ImageView mSearchImage;
  private EditText mSearchEdit;
  private RecyclerView mRecyclerView;


  public static HistoryFragment getInstance(boolean onlyFavorite) {
    HistoryFragment fragment = new HistoryFragment();

    Bundle arguments = new Bundle();
    arguments.putBoolean(KEY_ONLY_FAVORITE, onlyFavorite);
    fragment.setArguments(arguments);

    return fragment;
  }

  // ----------------------------------- lifecycle ------------------------------------------------

  @Override
  public void onCreate(Bundle savedInstanceState) {
    if (initializeType(getArguments())) {
      // state initialized
    } else if (initializeType(savedInstanceState)) {
      // state initialized
    } else {
      throw new IllegalStateException("Type of the HistoryFragment must be defined");
    }

    super.onCreate(savedInstanceState);
  }

  @Override
  public void onStart() {
    super.onStart();
  }

  @Override
  public void onResume() {
    super.onResume();

    if (!getUserVisibleHint()) {
      return;
    }

    getPresenter().initializeAdapter();
  }

  @Override
  public void setUserVisibleHint(boolean visible)
  {
    super.setUserVisibleHint(visible);
    if (visible && isResumed() && getUserVisibleHint()) {
      onResume();
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    saveType(outState);
  }

  // ------------------------------ IHistoryView methods ------------------------------------------

  @Override
  public void addTranslationsToAdapter(List<Translation> translations) {
    ((ITranslationsModel)mRecyclerView.getAdapter()).addTranslations(translations);
  }

  @Override
  public void clearAdapter() {
    ((ITranslationsModel)mRecyclerView.getAdapter()).clear();
  }

  @Override
  public int getTranslationCount() {
    return  ((ITranslationsModel)mRecyclerView.getAdapter()).getSize();
  }

  // --------------------------- PresenterFragment methods ----------------------------------------

  @Override
  protected IHistoryView getViewInterface() {
    return this;
  }

  @Override
  protected IPresenterFactory<HistoryPresenter, IHistoryView> getPresenterFactory() {
    // bind remote service
    ITranslateRemoteService remoteService = TranslateRemoteService.Factory.create(
        new OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build());

    // bind local service
    ITranslateLocalService localService = ((ITranslateLocalServiceProvider) getActivity()
        .getApplication())
        .getTranslateLocalService();

    // bind repository
    ITranslateRepository repository = TranslateRepository.Factory.create(localService, remoteService);

    // bind use case
    GetTranslationsUseCase useCase = new GetTranslationsUseCase(AsyncTask.THREAD_POOL_EXECUTOR,
        repository, mOnlyFavorite);

    return new HistoryPresenter.Factory(useCase);
  }

  @Override
  protected int getFragmentId() {
    return HistoryFragment.class.getSimpleName().hashCode();
  }

  @Override
  protected int getLayoutRes() {
    return R.layout.fragment_history;
  }

  @Override
  protected void initializeView(View view) {
    mSearchImage = (ImageView) view.findViewById(R.id.image_search);
    mSearchEdit = (EditText) view.findViewById(R.id.edit_search);
    mSearchEdit.setHint(mOnlyFavorite ? getString(R.string.hint_search_favorite_history) :
        getString(R.string.hint_search_all_history));
    mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    mRecyclerView.setAdapter(new TranslationsAdapter());
  }

  @Override
  protected void setListeners() {

  }

  // ---------------------------------- private methods -------------------------------------------

  private boolean initializeType(Bundle bundle) {
    if (bundle.containsKey(KEY_ONLY_FAVORITE)) {
      mOnlyFavorite = bundle.getBoolean(KEY_ONLY_FAVORITE);
      return true;
    }

    return false;
  }

  private void saveType(Bundle outState) {
    outState.putBoolean(KEY_ONLY_FAVORITE, mOnlyFavorite);
  }

  // ----------------------------------- inner classes --------------------------------------------

  private enum RecyclerViewState {
    INITIALIZED_FROM_BUNDLE,
    INITIALIZED_FROM_PRESENTER,
    NOT_INITIALIZED
  }
}
