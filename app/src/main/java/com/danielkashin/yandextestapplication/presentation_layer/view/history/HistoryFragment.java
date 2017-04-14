package com.danielkashin.yandextestapplication.presentation_layer.view.history;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.danielkashin.yandextestapplication.R;
import com.danielkashin.yandextestapplication.data_layer.services.translation.local.ITranslationLocalService;
import com.danielkashin.yandextestapplication.data_layer.services.translation.remote.ITranslationRemoteService;
import com.danielkashin.yandextestapplication.data_layer.services.translation.remote.TranslationRemoteService;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.danielkashin.yandextestapplication.domain_layer.repository.ITranslationRepository;
import com.danielkashin.yandextestapplication.domain_layer.repository.TranslationRepository;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.GetTranslationsUseCase;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.translations.ITranslationsModel;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.translations.TranslationsAdapter;
import com.danielkashin.yandextestapplication.presentation_layer.application.ITranslateLocalServiceProvider;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.IPresenterFactory;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.history.HistoryPresenter;
import com.danielkashin.yandextestapplication.presentation_layer.view.base.PresenterFragment;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.history_pager.IHistoryPage;
import com.danielkashin.yandextestapplication.presentation_layer.view.history_pager.IHistoryPagerView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;


public class HistoryFragment extends PresenterFragment<HistoryPresenter, IHistoryView>
    implements IHistoryView, IHistoryPage {

  private State mRestoredState;

  private ImageView mSearchImage;
  private EditText mSearchEdit;
  private TextView mNoContentText;
  private RecyclerView mRecyclerView;
  private RelativeLayout mSearchLayout;


  public static HistoryFragment getInstance(boolean onlyFavorite) {
    HistoryFragment fragment = new HistoryFragment();

    fragment.setArguments(State.saveOnlyFavorite(onlyFavorite));

    return fragment;
  }

  // ----------------------------------- lifecycle ------------------------------------------------

  @Override
  public void onCreate(Bundle savedInstanceState) {
    if (getParentFragment() == null || !(getParentFragment() instanceof IHistoryPagerView)) {
      throw new IllegalStateException("Parent fragment must implement IHistoryPagerView");
    }

    mRestoredState = new State(savedInstanceState);

    if (!mRestoredState.isTypeInitialized()) {
      mRestoredState = new State(getArguments());
    }

    if (!mRestoredState.isTypeInitialized()) {
      // we can`t know whether fragment is only favourite history or all history
      throw new IllegalStateException("Type of the HistoryFragment must be defined");
    }

    super.onCreate(savedInstanceState);
  }

  @Override
  public void onStart() {
    super.onStart();

    if (!mRestoredState.isAdapterInitialized()) {
      getPresenter().initializeAdapter();
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    State.saveToOutState(
        outState,
        mRestoredState.isOnlyFavorite(),
        mSearchLayout.getVisibility() == View.VISIBLE,
        mRecyclerView.getVisibility() == View.VISIBLE,
        mNoContentText.getVisibility() == View.VISIBLE,
        (ITranslationsModel) mRecyclerView.getAdapter());
  }

  // ------------------------------- IHistoryPage -------------------------------------------

  @Override
  public void onSelected() {
    if (mRecyclerView.getAdapter() != null && mRecyclerView.getAdapter().getItemCount() > 0) {
      ((IHistoryPagerView) getParentFragment()).showDeleteHistoryButton(this);
    } else {
      ((IHistoryPagerView) getParentFragment()).hideDeleteHistoryButton(this);
    }
  }

  @Override
  public void onDeleteButtonClicked() {

  }

  // ------------------------------ IHistoryView methods ------------------------------------------

  @Override
  public void showEmptyContentInterface() {
    ((IHistoryPagerView) getParentFragment()).hideDeleteHistoryButton(this);
    showEmptyContentViews();
  }

  @Override
  public void showNotEmptyContentInterface() {
    ((IHistoryPagerView) getParentFragment()).showDeleteHistoryButton(this);
    showNotEmptyContentViews();
  }

  @Override
  public void showEmptySearchContentInterface() {
    mRecyclerView.setVisibility(View.INVISIBLE);
    mNoContentText.setText(getString(R.string.no_content_search_message));
    mNoContentText.setVisibility(View.VISIBLE);
  }

  @Override
  public void addTranslationsToAdapter(List<Translation> translations, boolean clear) {
    ((ITranslationsModel) mRecyclerView.getAdapter()).addTranslations(translations, clear);
  }

  @Override
  public int getTranslationCount() {
    if (mRecyclerView == null || mRecyclerView.getAdapter() == null) {
      return 0;
    } else {
      return ((ITranslationsModel) mRecyclerView.getAdapter()).getSize();
    }
  }

  // --------------------------- PresenterFragment methods ----------------------------------------

  @Override
  protected IHistoryView getViewInterface() {
    return this;
  }

  @Override
  protected IPresenterFactory<HistoryPresenter, IHistoryView> getPresenterFactory() {
    // bind remote service
    ITranslationRemoteService remoteService = TranslationRemoteService.Factory.create(
        new OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build());

    // bind local service
    ITranslationLocalService localService = ((ITranslateLocalServiceProvider) getActivity()
        .getApplication())
        .getTranslateLocalService();

    // bind repository
    ITranslationRepository repository = TranslationRepository.Factory.create(localService, remoteService);

    // bind use case
    GetTranslationsUseCase useCase = new GetTranslationsUseCase(AsyncTask.THREAD_POOL_EXECUTOR,
        repository, mRestoredState.isOnlyFavorite());

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
    mSearchLayout = (RelativeLayout) view.findViewById(R.id.layout_search);
    mSearchImage = (ImageView) view.findViewById(R.id.image_search);
    mSearchEdit = (EditText) view.findViewById(R.id.edit_search);
    mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    mNoContentText = (TextView) view.findViewById(R.id.text_no_content);

    mSearchEdit.setHint(mRestoredState.isOnlyFavorite() ? getString(R.string.hint_search_favorite_history) :
        getString(R.string.hint_search_all_history));

    mRecyclerView.setAdapter(
        mRestoredState.isAdapterInitialized()
            ? (RecyclerView.Adapter) mRestoredState.getAdapter()
            : new TranslationsAdapter());

    mNoContentText.setText(mRestoredState.isOnlyFavorite() ? getString(R.string.no_content_favorites_message) :
        getString(R.string.no_content_history_message));

    if (mRestoredState.isViewVisibilityInitialized()) {
      mSearchLayout.setVisibility(mRestoredState.isSearchLayoutVisible() ? View.VISIBLE : View.GONE);
      mRecyclerView.setVisibility(mRestoredState.isRecyclerViewVisible() ? View.VISIBLE : View.GONE);
      mNoContentText.setVisibility(mRestoredState.isNoContentTextVisible() ? View.VISIBLE : View.GONE);
    }
  }

  // ---------------------------------- private methods -------------------------------------------

  private void showEmptyContentViews() {
    mSearchLayout.setVisibility(View.GONE);

    mRecyclerView.setVisibility(View.INVISIBLE);

    mNoContentText.setText(getString(mRestoredState.isOnlyFavorite() ? R.string.no_content_favorites_message
        : R.string.no_content_history_message));
    mNoContentText.setVisibility(View.VISIBLE);
  }

  private void showNotEmptyContentViews() {
    mSearchLayout.setVisibility(View.VISIBLE);

    mRecyclerView.setVisibility(View.VISIBLE);

    mNoContentText.setVisibility(View.GONE);
  }

  // ----------------------------------- inner classes --------------------------------------------

  private static class State {
    private static final String KEY_ONLY_FAVORITE = "KEY_ONLY_FAVORITE";
    private static final String KEY_SEARCH_LAYOUT_VISIBLE = "KEY_SEARCH_LAYOUT_VISIBLE";
    private static final String KEY_RECYCLER_VIEW_VISIBLE = "KEY_RECYCLER_VIEW_VISIBLE";
    private static final String KEY_NO_CONTENT_TEXT_VISIBLE = "KEY_NO_CONTENT_TEXT_VISIBLE";

    private boolean typeInitialized;
    private boolean onlyFavorite;

    private boolean adapterInitialized;
    private ITranslationsModel adapter;

    private boolean viewVisibilityInitialized;
    private boolean searchLayoutVisible;
    private boolean recyclerViewVisible;
    private boolean noContentTextVisible;


    public State(Bundle savedInstanceState) {
      if (savedInstanceState == null) {
        return;
      }

      // initialize type
      if (savedInstanceState.containsKey(KEY_ONLY_FAVORITE)) {
        onlyFavorite = savedInstanceState.getBoolean(KEY_ONLY_FAVORITE);
        typeInitialized = true;
      } else {
        typeInitialized = false;
        return;
      }

      // initialize adapter
      try {
        adapter = new TranslationsAdapter(savedInstanceState);
        adapterInitialized = true;
      } catch (IllegalStateException e) {
        adapterInitialized = false;
      }

      // initialize view visibilities
      if (savedInstanceState.containsKey(KEY_SEARCH_LAYOUT_VISIBLE)
          && savedInstanceState.containsKey(KEY_RECYCLER_VIEW_VISIBLE)
          && savedInstanceState.containsKey(KEY_NO_CONTENT_TEXT_VISIBLE)) {
        viewVisibilityInitialized = true;
        searchLayoutVisible = savedInstanceState.getBoolean(KEY_SEARCH_LAYOUT_VISIBLE);
        recyclerViewVisible = savedInstanceState.getBoolean(KEY_SEARCH_LAYOUT_VISIBLE);
        noContentTextVisible = savedInstanceState.getBoolean(KEY_NO_CONTENT_TEXT_VISIBLE);
      }
    }

    public boolean isTypeInitialized() {
      return typeInitialized;
    }

    public boolean isAdapterInitialized() {
      return adapterInitialized;
    }

    public boolean isViewVisibilityInitialized(){
      return viewVisibilityInitialized;
    }

    public boolean isOnlyFavorite() {
      return onlyFavorite;
    }

    public ITranslationsModel getAdapter() {
      return adapter;
    }

    public boolean isSearchLayoutVisible() {
      return searchLayoutVisible;
    }

    public boolean isRecyclerViewVisible() {
      return recyclerViewVisible;
    }

    public boolean isNoContentTextVisible() {
      return noContentTextVisible;
    }

    public static void saveToOutState(Bundle outState,
                                      boolean onlyFavorite,
                                      boolean searchLayoutVisible,
                                      boolean recyclerViewVisible,
                                      boolean noContentTextVisible,
                                      ITranslationsModel adapter) {
      outState.putBoolean(KEY_ONLY_FAVORITE, onlyFavorite);
      outState.putBoolean(KEY_SEARCH_LAYOUT_VISIBLE, searchLayoutVisible);
      outState.putBoolean(KEY_RECYCLER_VIEW_VISIBLE, recyclerViewVisible);
      outState.putBoolean(KEY_NO_CONTENT_TEXT_VISIBLE, noContentTextVisible);
      adapter.onSaveInstanceState(outState);
    }

    public static Bundle saveOnlyFavorite(boolean onlyFavorite) {
      Bundle bundle = new Bundle();
      bundle.putBoolean(KEY_ONLY_FAVORITE, onlyFavorite);
      return bundle;
    }

  }

}
