package com.danielkashin.yandextestapplication.presentation_layer.view.history;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.danielkashin.yandextestapplication.R;
import com.danielkashin.yandextestapplication.data_layer.services.translate.local.ITranslateLocalService;
import com.danielkashin.yandextestapplication.data_layer.services.translate.remote.ITranslateRemoteService;
import com.danielkashin.yandextestapplication.data_layer.services.translate.remote.TranslateRemoteService;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.danielkashin.yandextestapplication.domain_layer.repository.translate.ITranslateRepository;
import com.danielkashin.yandextestapplication.domain_layer.repository.translate.TranslateRepository;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.GetTranslationsUseCase;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.base.IDatabaseChangeReceiver;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.translations.ITranslationsCallbacks;
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


  // ---------------------------------- getInstance -----------------------------------------------

  public static HistoryFragment getInstance(State.FragmentType fragmentType) {
    HistoryFragment fragment = new HistoryFragment();

    fragment.setArguments(State.getFragmentTypeBundle(fragmentType));

    return fragment;
  }

  // ----------------------------------- lifecycle ------------------------------------------------

  @Override
  public void onCreate(Bundle savedInstanceState) {
    if (!(getParentFragment() instanceof IHistoryPagerView)
        || !(getParentFragment() instanceof IDatabaseChangeReceiver)) {
      throw new IllegalStateException("Parent fragment must implement IHistoryPagerView" +
          " and IDatabaseChangeReceiver");
    }

    mRestoredState = new State(savedInstanceState);

    if (!mRestoredState.isFragmentTypeInitialized()) {
      mRestoredState = new State(getArguments());
    }

    if (!mRestoredState.isFragmentTypeInitialized()) {
      // we can`t know whether fragment is only favourite history or all history
      throw new IllegalStateException("Type of the HistoryFragment must be defined");
    }

    // we must know the fragmentType of the fragment before its presenter will be created in base class
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onStart() {
    super.onStart();

    if (!mRestoredState.isTranslationsAdapterInitialized()) {
      getPresenter().refreshTranslations(null);
    }

    setAdapterCallbacks();
    setListeners();
  }

  @Override
  public void onStop() {
    super.onStop();

    removeAdapterCallbacks();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    mRestoredState.setTranslationsAdapter((ITranslationsModel) mRecyclerView.getAdapter());
    mRestoredState.setNoContentTextVisible(mNoContentText.getVisibility() == View.VISIBLE);
    mRestoredState.setRecyclerViewVisible(mRecyclerView.getVisibility() == View.VISIBLE);
    mRestoredState.setSearchLayoutVisible(mSearchLayout.getVisibility() == View.VISIBLE);
    mRestoredState.saveToOutState(outState);
  }

  // ------------------------------- IDatabaseChangeReceiver --------------------------------------

  @Override
  public void receiveOnDataChanged(IDatabaseChangeReceiver source) {
    getPresenter().refreshTranslations(null);
  }

  // ------------------------------- IDatabaseChangePublisher --------------------------------------

  @Override
  public void publishOnDataChanged() {

  }

  // ------------------------------------- IHistoryPage -------------------------------------------

  @Override
  public void onAnotherPageSelected() {
    if (mSearchEdit != null && !mSearchEdit.getText().toString().equals("")) {
      mSearchEdit.setText("");
    }
  }

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
    mSearchLayout.setVisibility(View.GONE);
    mRecyclerView.setVisibility(View.INVISIBLE);
    mNoContentText.setVisibility(View.VISIBLE);
  }

  @Override
  public void showNotEmptyContentInterface() {
    ((IHistoryPagerView) getParentFragment()).showDeleteHistoryButton(this);
    mSearchLayout.setVisibility(View.VISIBLE);
    mRecyclerView.setVisibility(View.VISIBLE);
    mNoContentText.setVisibility(View.GONE);
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
  public int getTranslationAdapterCount() {
    if (mRecyclerView == null || mRecyclerView.getAdapter() == null) {
      return 0;
    } else {
      return ((ITranslationsModel) mRecyclerView.getAdapter()).getSize();
    }
  }

  @Override
  public void clearTranslationAdapter() {
    ((ITranslationsModel) mRecyclerView.getAdapter()).clear();
  }

  // --------------------------- PresenterFragment methods ----------------------------------------

  @Override
  protected IHistoryView getViewInterface() {
    return this;
  }

  @Override
  protected IPresenterFactory<HistoryPresenter, IHistoryView> getPresenterFactory() {
    // bind services
    ITranslateRemoteService remoteService = TranslateRemoteService.Factory.create(
        new OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build());

    ITranslateLocalService localService = ((ITranslateLocalServiceProvider) getActivity()
        .getApplication())
        .getTranslateLocalService();


    // bind repository
    ITranslateRepository repository = TranslateRepository.Factory.create(localService, remoteService);


    // bind use case
    GetTranslationsUseCase useCase = new GetTranslationsUseCase(AsyncTask.THREAD_POOL_EXECUTOR,
        repository, mRestoredState.getFragmentType());


    // return presenter with dependencies
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

    mRecyclerView.setAdapter(mRestoredState.isTranslationsAdapterInitialized()
        ? (RecyclerView.Adapter) mRestoredState.getTranslationsAdapter()
        : new TranslationsAdapter());

    mSearchEdit.setHint(mRestoredState.getFragmentType() == State.FragmentType.ONLY_FAVORITE_HISTORY
        ? getString(R.string.hint_search_favorite_history)
        : getString(R.string.hint_search_all_history));

    mNoContentText.setText(mRestoredState.getFragmentType() == State.FragmentType.ONLY_FAVORITE_HISTORY
        ? getString(R.string.no_content_favorites_message)
        : getString(R.string.no_content_history_message));

    mSearchLayout.setVisibility(mRestoredState.isSearchLayoutVisible() ? View.VISIBLE : View.GONE);
    mRecyclerView.setVisibility(mRestoredState.isRecyclerViewVisible() ? View.VISIBLE : View.GONE);
    mNoContentText.setVisibility(mRestoredState.isNoContentTextVisible() ? View.VISIBLE : View.GONE);
  }

  // ---------------------------------- private methods -------------------------------------------

  private void setListeners() {
    mSearchEdit.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      @Override
      public void afterTextChanged(Editable editable) {
        getPresenter().refreshTranslations(editable.toString());
      }
    });
  }

  private void setAdapterCallbacks() {
    ((ITranslationsModel) mRecyclerView.getAdapter()).addCallbacks(new ITranslationsCallbacks() {
      @Override
      public void onFavoriteToggleClicked(int position) {
        Toast.makeText(getContext(), "ICON " + position, Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onItemClicked(int position) {
        Toast.makeText(getContext(), "SHORT CLICK " + position, Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onLongItemClicked(int position) {
        Toast.makeText(getContext(), "LONG CLICK " + position, Toast.LENGTH_SHORT).show();
      }
    });
  }

  private void removeAdapterCallbacks() {
    ((ITranslationsModel) mRecyclerView.getAdapter()).removeCallbacks();
  }

  // ----------------------------------- inner classes --------------------------------------------

  public static class State {
    private static final String KEY_ONLY_FAVORITE = "KEY_ONLY_FAVORITE";
    private static final String KEY_SEARCH_LAYOUT_VISIBLE = "KEY_SEARCH_LAYOUT_VISIBLE";
    private static final String KEY_RECYCLER_VIEW_VISIBLE = "KEY_RECYCLER_VIEW_VISIBLE";
    private static final String KEY_NO_CONTENT_TEXT_VISIBLE = "KEY_NO_CONTENT_TEXT_VISIBLE";

    private FragmentType fragmentType;
    private ITranslationsModel translationsAdapter;
    private boolean searchLayoutVisible;
    private boolean recyclerViewVisible;
    private boolean noContentTextVisible;


    private State(Bundle savedInstanceState) {
      if (savedInstanceState == null || !savedInstanceState.containsKey(KEY_ONLY_FAVORITE)
          || savedInstanceState.getSerializable(KEY_ONLY_FAVORITE) == null) {
        fragmentType = null;
        return;
      }

      fragmentType = (FragmentType) savedInstanceState.getSerializable(KEY_ONLY_FAVORITE);

      // initialize translationsAdapter
      try {
        translationsAdapter = new TranslationsAdapter(savedInstanceState);
      } catch (IllegalStateException e) {
        translationsAdapter = null;
      }

      // initialize view visibilities
      if (savedInstanceState.containsKey(KEY_SEARCH_LAYOUT_VISIBLE)
          && savedInstanceState.containsKey(KEY_RECYCLER_VIEW_VISIBLE)
          && savedInstanceState.containsKey(KEY_NO_CONTENT_TEXT_VISIBLE)) {
        searchLayoutVisible = savedInstanceState.getBoolean(KEY_SEARCH_LAYOUT_VISIBLE);
        recyclerViewVisible = savedInstanceState.getBoolean(KEY_RECYCLER_VIEW_VISIBLE);
        noContentTextVisible = savedInstanceState.getBoolean(KEY_NO_CONTENT_TEXT_VISIBLE);
      }
    }

    private void saveToOutState(Bundle outState) {
      outState.putSerializable(KEY_ONLY_FAVORITE, this.fragmentType);
      outState.putBoolean(KEY_SEARCH_LAYOUT_VISIBLE, this.searchLayoutVisible);
      outState.putBoolean(KEY_RECYCLER_VIEW_VISIBLE, this.recyclerViewVisible);
      outState.putBoolean(KEY_NO_CONTENT_TEXT_VISIBLE, this.noContentTextVisible);

      if (translationsAdapter != null) {
        translationsAdapter.onSaveInstanceState(outState);
      }
    }

    //                        ------------ setters --------------

    private void setFragmentType(FragmentType fragmentType) {
      this.fragmentType = fragmentType;
    }

    private void setSearchLayoutVisible(boolean searchLayoutVisible) {
      this.searchLayoutVisible = searchLayoutVisible;
    }

    private void setRecyclerViewVisible(boolean recyclerViewVisible) {
      this.recyclerViewVisible = recyclerViewVisible;
    }

    private void setNoContentTextVisible(boolean noContentTextVisible) {
      this.noContentTextVisible = noContentTextVisible;
    }

    private void setTranslationsAdapter(ITranslationsModel translationsAdapter) {
      this.translationsAdapter = translationsAdapter;
    }

    //                        ------------ getters --------------

    private boolean isFragmentTypeInitialized() {
      return fragmentType != null;
    }

    private boolean isTranslationsAdapterInitialized() {
      return translationsAdapter != null;
    }

    private FragmentType getFragmentType() {
      return fragmentType;
    }

    private ITranslationsModel getTranslationsAdapter() {
      return translationsAdapter;
    }

    private boolean isSearchLayoutVisible() {
      return searchLayoutVisible;
    }

    private boolean isRecyclerViewVisible() {
      return recyclerViewVisible;
    }

    private boolean isNoContentTextVisible() {
      return noContentTextVisible;
    }

    //                        --------------- static ------------------

    private static Bundle getFragmentTypeBundle(FragmentType fragmentType) {
      Bundle bundle = new Bundle();
      bundle.putSerializable(KEY_ONLY_FAVORITE, fragmentType);
      return bundle;
    }

    //                        ------------ inner types ----------------

    public enum FragmentType {
      ONLY_FAVORITE_HISTORY,
      ALL_HISTORY
    }

  }

}
