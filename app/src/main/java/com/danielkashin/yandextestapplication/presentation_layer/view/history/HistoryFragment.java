package com.danielkashin.yandextestapplication.presentation_layer.view.history;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.danielkashin.yandextestapplication.R;
import com.danielkashin.yandextestapplication.data_layer.services.translate.local.ITranslationsLocalService;
import com.danielkashin.yandextestapplication.data_layer.services.translate.remote.ITranslationsRemoteService;
import com.danielkashin.yandextestapplication.data_layer.services.translate.remote.TranslationsRemoteService;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.danielkashin.yandextestapplication.domain_layer.repository.translate.ITranslationsRepository;
import com.danielkashin.yandextestapplication.domain_layer.repository.translate.TranslationsRepository;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.DeleteTranslationUseCase;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.DeleteTranslationsUseCase;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.GetTranslationsUseCase;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.RefreshTranslationUseCase;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.base.IDatabaseChangePublisher;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.base.IDatabaseChangeReceiver;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.translations.ITranslationsAdapter;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.translations.TranslationsAdapter;
import com.danielkashin.yandextestapplication.presentation_layer.application.ITranslateLocalServiceProvider;
import com.danielkashin.yandextestapplication.presentation_layer.custom_views.DividerItemDecoration;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.IPresenterFactory;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.history.HistoryPresenter;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.history.IHistoryPresenter;
import com.danielkashin.yandextestapplication.presentation_layer.view.base.PresenterFragment;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.history_pager.IHistoryPage;
import com.danielkashin.yandextestapplication.presentation_layer.view.history_pager.IHistoryPagerView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;


public class HistoryFragment extends PresenterFragment<HistoryPresenter, IHistoryView>
    implements IHistoryView, IHistoryPage, IDatabaseChangePublisher, ITranslationsAdapter.Callbacks {

  private ImageView mSearchImage;
  private EditText mSearchEdit;
  private TextView mNoContentText;
  private RecyclerView mRecyclerView;
  private RelativeLayout mSearchLayout;
  private ImageView mClearImage;
  private View mDivider;

  private State mRestoredState;
  private boolean mRefreshWhenNavigatingToAnotherPage;
  private RecyclerView.OnScrollListener mRecyclerViewScrollListener;


  // ---------------------------------- getInstance -----------------------------------------------

  public static HistoryFragment getInstance(State.FragmentType fragmentType) {
    HistoryFragment fragment = new HistoryFragment();

    fragment.setArguments(State.getFragmentTypeBundle(fragmentType));

    return fragment;
  }

  // ----------------------------------- lifecycle ------------------------------------------------

  @Override
  public void onCreate(Bundle savedInstanceState) {
    if (!(getParentFragment() instanceof IDatabaseChangePublisher)
        || !(getParentFragment() instanceof IHistoryPagerView)) {
      throw new IllegalStateException("Parent fragment must implement IHistoryPagerView" +
          " and IDatabaseChangePublisher");
    }

    mRestoredState = new State(savedInstanceState);

    if (!mRestoredState.isFragmentTypeInitialized()) {
      mRestoredState = new State(getArguments());
    }

    if (!mRestoredState.isFragmentTypeInitialized()) {
      // we can`t know whether fragment is only favourite history or all history
      throw new IllegalStateException("Type of the HistoryFragment must be defined");
    }

    // we must know the type of the fragment before its presenter will be created in base class
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onStart() {
    super.onStart();

    if (!(getPresenter() instanceof IHistoryPresenter)) {
      throw new IllegalStateException("Presenter of IHistoryView must be an instance of IHistoryPresenter");
    }

    if (!mRestoredState.isTranslationsAdapterInitialized()) {
      ((IHistoryPresenter) getPresenter()).onUploadTranslations(0, null);
    } else {
      setAdapterCallbacks();
      setRecyclerViewScrollListener();
    }

    setListeners();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    // refresh and save state
    mRestoredState.setTranslationsAdapter((ITranslationsAdapter) mRecyclerView.getAdapter());
    mRestoredState.setNoContentTextVisible(mNoContentText.getVisibility() == View.VISIBLE);
    mRestoredState.setRecyclerViewVisible(mRecyclerView.getVisibility() == View.VISIBLE);
    mRestoredState.setSearchLayoutVisible(mSearchLayout.getVisibility() == View.VISIBLE);
    mRestoredState.setClearImageVisible(mClearImage.getVisibility() == View.VISIBLE);
    mRestoredState.saveToOutState(outState);
  }

  // ------------------------------- IDatabaseChangeReceiver --------------------------------------

  @Override
  public void receiveOnDataChanged(IDatabaseChangeReceiver source) {
    // data refresh is needed
    ((IHistoryPresenter) getPresenter()).onUploadTranslations(0, null);
  }

  // ------------------------------- IDatabaseChangePublisher -------------------------------------

  @Override
  public void publishOnDataChanged(IDatabaseChangePublisher source) {
    // notificate parent fragment that database was changed
    ((IDatabaseChangePublisher) getParentFragment()).publishOnDataChanged(source);
  }

  // ----------------------------- ITranslationsAdapter callbacks ------------------------------------

  @Override
  public void onToggleFavoriteClicked(Translation translation) {
    ((IHistoryPresenter) getPresenter()).onAdapterToggleClicked(translation);
  }

  @Override
  public void onItemClicked(Translation translation) {
    ((IHistoryPagerView) getParentFragment()).openTranslatePage(translation);
  }

  @Override
  public void onLongItemClicked(final Translation translation) {
    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(),
        android.R.layout.select_dialog_item);
    arrayAdapter.add(getString(R.string.delete));

    new AlertDialog.Builder(getContext())
        .setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            String name = arrayAdapter.getItem(which);
            if (name != null && name.equals(getString(R.string.delete))) {
              ((IHistoryPresenter) getPresenter()).onAdapterItemLongClicked(translation);
            }
          }
        })
        .create()
        .show();
  }

  // ------------------------------------- IHistoryPage -------------------------------------------

  @Override
  public void onAnotherPageSelected() {
    if (mSearchEdit != null && !mSearchEdit.getText().toString().equals("")) {
      mSearchEdit.setText("");
    } else if (mRestoredState.getFragmentType() == State.FragmentType.ONLY_FAVORITE_HISTORY
        && mRefreshWhenNavigatingToAnotherPage) {
      mRefreshWhenNavigatingToAnotherPage = false;
      ((IHistoryPresenter) getPresenter()).onUploadTranslations(0, null);
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
    if (mRestoredState.getFragmentType() == State.FragmentType.ALL_HISTORY
        && ((ITranslationsAdapter) mRecyclerView.getAdapter()).containsOnlyFavoriteTranslations()) {
      showDeleteTranslationsSourceIsEmpty();
    } else {
      int messageId = mRestoredState.getFragmentType() == State.FragmentType.ONLY_FAVORITE_HISTORY
          ? R.string.delete_only_favorite_history_confirmation_message
          : R.string.delete_all_history_confirmation_message;

      new AlertDialog.Builder(getContext())
          .setTitle(R.string.history)
          .setMessage(messageId)
          .setPositiveButton(R.string.confirmation_confirm,
              new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  ((IHistoryPresenter) getPresenter()).onDeleteTranslations();
                }
              })
          .setNegativeButton(R.string.confirmation_cancel,
              new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  dialog.cancel();
                }
              })
          .create()
          .show();
    }
  }

  // ------------------------------ IHistoryView methods ------------------------------------------

  @Override
  public void setAdapterEndReached() {
    ((ITranslationsAdapter) mRecyclerView.getAdapter()).setEndReachedTrue();
  }

  @Override
  public void setRecyclerViewScrollListener() {
    mRecyclerViewScrollListener = new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        int lastVisibleItem = manager.findLastVisibleItemPosition();

        if (((ITranslationsAdapter) mRecyclerView.getAdapter()).isDataUploadingToEndNeeded(lastVisibleItem)) {
          // try upload data to the end of adapter

          String searchRequest = mSearchEdit.getText().toString();

          ITranslationsAdapter adapter = (ITranslationsAdapter) mRecyclerView.getAdapter();
          adapter.setDataUploadingToEndTrue();
          int translationCount = adapter.getSize();

          ((IHistoryPresenter) getPresenter()).onUploadTranslations(translationCount, searchRequest);
        }
      }
    };

    mRecyclerView.addOnScrollListener(mRecyclerViewScrollListener);
  }

  @Override
  public void removeRecyclerViewScrollListener() {
    mRecyclerView.removeOnScrollListener(mRecyclerViewScrollListener);
  }

  @Override
  public void onDeleteTranslationSuccess(Translation translation) {
    publishOnDataChanged(this);
    ((ITranslationsAdapter) mRecyclerView.getAdapter()).deleteTranslation(translation);

    if (((ITranslationsAdapter) mRecyclerView.getAdapter()).getSize() == 0) {
      if (mSearchEdit.getText().toString().isEmpty()) {
        showEmptyContentInterface();
      } else {
        showEmptySearchContentInterface();
      }
    }
  }

  @Override
  public void onDeleteTranslationsSuccess() {
    publishOnDataChanged(this);

    if (mSearchEdit != null && !mSearchEdit.getText().toString().equals("")) {
      mSearchEdit.setText("");
    } else {
      ((IHistoryPresenter) getPresenter()).onUploadTranslations(0, null);
    }
  }

  @Override
  public String getStringById(int id) {
    return getResources().getString(id);
  }

  @Override
  public void showDeleteTranslationsSourceIsEmpty() {
    if (mRestoredState.getFragmentType() == State.FragmentType.ALL_HISTORY) {
      showAlertDialog(getStringById(R.string.delete_translations_source_is_empty_all_history));
    }
  }

  @Override
  public void onTranslationRefreshedSuccess() {
    mRefreshWhenNavigatingToAnotherPage = true;
    publishOnDataChanged(this);
  }

  @Override
  public void showAlertDialog(String text) {
    new AlertDialog.Builder(getContext())
        .setMessage(text)
        .create()
        .show();
  }

  @Override
  public void setAdapterCallbacks() {
    ((ITranslationsAdapter) mRecyclerView.getAdapter()).addCallbacks(this);
  }

  @Override
  public void removeAdapterCallbacks() {
    ((ITranslationsAdapter) mRecyclerView.getAdapter()).removeCallbacks();
  }

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
    mNoContentText.setText(mRestoredState.getFragmentType() == State.FragmentType.ONLY_FAVORITE_HISTORY
        ? getString(R.string.no_content_favorites_message)
        : getString(R.string.no_content_history_message));
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
    ((ITranslationsAdapter) mRecyclerView.getAdapter()).addTranslations(translations, clear);
  }

  @Override
  public void clearAdapter() {
    ((ITranslationsAdapter) mRecyclerView.getAdapter()).clear();
  }

  // --------------------------- PresenterFragment methods ----------------------------------------

  @Override
  protected IHistoryView getViewInterface() {
    return this;
  }

  @Override
  protected IPresenterFactory<HistoryPresenter, IHistoryView> getPresenterFactory() {
    // bind services
    ITranslationsRemoteService remoteService = TranslationsRemoteService.Factory.create(
        new OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build());

    ITranslationsLocalService localService = ((ITranslateLocalServiceProvider) getActivity()
        .getApplication())
        .getTranslateLocalService();


    // bind repository
    ITranslationsRepository repository = TranslationsRepository.Factory.create(localService, remoteService);


    // bind use cases
    GetTranslationsUseCase getTranslationsUseCase = new GetTranslationsUseCase(
        AsyncTask.THREAD_POOL_EXECUTOR,
        repository,
        mRestoredState.getFragmentType());

    DeleteTranslationsUseCase deleteTranslationsUseCase = new DeleteTranslationsUseCase(
        AsyncTask.THREAD_POOL_EXECUTOR,
        repository,
        mRestoredState.getFragmentType());

    RefreshTranslationUseCase refreshTranslationUseCase = new RefreshTranslationUseCase(
        AsyncTask.THREAD_POOL_EXECUTOR,
        repository);

    DeleteTranslationUseCase deleteTranslationUseCase = new DeleteTranslationUseCase(
        AsyncTask.THREAD_POOL_EXECUTOR,
        repository);


    // return presenter with dependencies
    return new HistoryPresenter.Factory(getTranslationsUseCase, deleteTranslationsUseCase,
        refreshTranslationUseCase, deleteTranslationUseCase);
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
    mClearImage = (ImageView) view.findViewById(R.id.image_clear);
    mDivider = view.findViewById(R.id.divider);

    mSearchLayout.setVisibility(mRestoredState.isSearchLayoutVisible() ? View.VISIBLE : View.GONE);
    mRecyclerView.setVisibility(mRestoredState.isRecyclerViewVisible() ? View.VISIBLE : View.GONE);
    mNoContentText.setVisibility(mRestoredState.isNoContentTextVisible() ? View.VISIBLE : View.GONE);
    mClearImage.setVisibility(mRestoredState.isClearImageVisible() ? View.VISIBLE : View.INVISIBLE);


    if (mRestoredState.isTranslationsAdapterInitialized()) {
      mRecyclerView.setAdapter((RecyclerView.Adapter) mRestoredState.getTranslationsAdapter());
    } else {
      mRecyclerView.setAdapter(new TranslationsAdapter());
    }
    mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), R.drawable.divider_bold));

    mSearchEdit.setHint(mRestoredState.getFragmentType() == State.FragmentType.ONLY_FAVORITE_HISTORY
        ? getString(R.string.hint_search_favorite_history)
        : getString(R.string.hint_search_all_history));

    mNoContentText.setText(mRestoredState.getFragmentType() == State.FragmentType.ONLY_FAVORITE_HISTORY
        ? getString(R.string.no_content_favorites_message)
        : getString(R.string.no_content_history_message));
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
        if (editable.toString().isEmpty()) {
          mClearImage.setVisibility(View.INVISIBLE);
        } else {
          mClearImage.setVisibility(View.VISIBLE);
        }

        ((IHistoryPresenter) getPresenter()).onUploadTranslations(0, editable.toString());
      }
    });

    mClearImage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mSearchEdit.setText("");
      }
    });

    mSearchEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
          mDivider.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.divider_primary_color));
          mDivider.setMinimumHeight(2);
        } else {
          mDivider.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.divider_bold));
          mDivider.setMinimumHeight(1);
        }
      }
    });
  }

  // ----------------------------------- inner classes --------------------------------------------

  public static class State {
    private static final String KEY_ONLY_FAVORITE = "KEY_ONLY_FAVORITE";
    private static final String KEY_SEARCH_LAYOUT_VISIBLE = "KEY_SEARCH_LAYOUT_VISIBLE";
    private static final String KEY_RECYCLER_VIEW_VISIBLE = "KEY_RECYCLER_VIEW_VISIBLE";
    private static final String KEY_NO_CONTENT_TEXT_VISIBLE = "KEY_NO_CONTENT_TEXT_VISIBLE";
    private static final String KEY_CLEAR_IMAGE_VISIBLE = "KEY_CLEAR_IMAGE_VISIBLE";

    private FragmentType fragmentType;
    private ITranslationsAdapter translationsAdapter;
    private boolean searchLayoutVisible;
    private boolean recyclerViewVisible;
    private boolean noContentTextVisible;
    private boolean clearImageVisible;


    private State(Bundle savedInstanceState) {
      if (savedInstanceState == null || !savedInstanceState.containsKey(KEY_ONLY_FAVORITE)
          || savedInstanceState.getSerializable(KEY_ONLY_FAVORITE) == null) {
        fragmentType = null;
        return;
      }

      fragmentType = (FragmentType) savedInstanceState.getSerializable(KEY_ONLY_FAVORITE);

      // initialize translationsAdapter
      translationsAdapter = new TranslationsAdapter(savedInstanceState);
      if (!translationsAdapter.isInitialized()) {
        translationsAdapter = null;
      }

      // initialize view visibilities
      if (savedInstanceState.containsKey(KEY_SEARCH_LAYOUT_VISIBLE)
          && savedInstanceState.containsKey(KEY_RECYCLER_VIEW_VISIBLE)
          && savedInstanceState.containsKey(KEY_NO_CONTENT_TEXT_VISIBLE)
          && savedInstanceState.containsKey(KEY_CLEAR_IMAGE_VISIBLE)) {
        searchLayoutVisible = savedInstanceState.getBoolean(KEY_SEARCH_LAYOUT_VISIBLE);
        recyclerViewVisible = savedInstanceState.getBoolean(KEY_RECYCLER_VIEW_VISIBLE);
        noContentTextVisible = savedInstanceState.getBoolean(KEY_NO_CONTENT_TEXT_VISIBLE);
        clearImageVisible = savedInstanceState.getBoolean(KEY_CLEAR_IMAGE_VISIBLE);
      }
    }

    private void saveToOutState(Bundle outState) {
      outState.putSerializable(KEY_ONLY_FAVORITE, this.fragmentType);
      outState.putBoolean(KEY_SEARCH_LAYOUT_VISIBLE, this.searchLayoutVisible);
      outState.putBoolean(KEY_RECYCLER_VIEW_VISIBLE, this.recyclerViewVisible);
      outState.putBoolean(KEY_NO_CONTENT_TEXT_VISIBLE, this.noContentTextVisible);
      outState.putBoolean(KEY_CLEAR_IMAGE_VISIBLE, this.clearImageVisible);

      if (translationsAdapter != null) {
        translationsAdapter.onSaveInstanceState(outState);
      }
    }

    //                        ------------ setters --------------

    private void setClearImageVisible(boolean clearImageVisible) {
      this.clearImageVisible = clearImageVisible;
    }

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

    private void setTranslationsAdapter(ITranslationsAdapter translationsAdapter) {
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

    private ITranslationsAdapter getTranslationsAdapter() {
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

    private boolean isClearImageVisible() {
      return clearImageVisible;
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
