package com.danielkashin.yandextestapplication.presentation_layer.view.translate;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.danielkashin.yandextestapplication.R;
import com.danielkashin.yandextestapplication.data_layer.managers.network.INetworkManager;
import com.danielkashin.yandextestapplication.data_layer.managers.network.NetworkManager;
import com.danielkashin.yandextestapplication.data_layer.services.languages.local.ILanguagesLocalService;
import com.danielkashin.yandextestapplication.data_layer.services.languages.local.LanguagesLocalService;
import com.danielkashin.yandextestapplication.data_layer.services.translate.local.ITranslationsLocalService;
import com.danielkashin.yandextestapplication.data_layer.services.translate.remote.ITranslationsRemoteService;
import com.danielkashin.yandextestapplication.data_layer.services.translate.remote.TranslationsRemoteService;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Language;
import com.danielkashin.yandextestapplication.domain_layer.pojo.LanguagePair;
import com.danielkashin.yandextestapplication.data_layer.repository.languages.ILanguagesRepository;
import com.danielkashin.yandextestapplication.data_layer.repository.languages.LanguagesRepository;
import com.danielkashin.yandextestapplication.data_layer.repository.translate.ITranslationsRepository;
import com.danielkashin.yandextestapplication.data_layer.repository.translate.TranslationsRepository;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.GetLanguagesFromTranslationUseCase;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.GetLastTranslationUseCase;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.GetRefreshedTranslationUseCase;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.SaveTranslationUseCase;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.TranslateUseCase;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.base.IDatabaseChangePublisher;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.base.IDatabaseChangeReceiver;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.base.ITranslateHolder;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.main_pager.IMainPage;
import com.danielkashin.yandextestapplication.presentation_layer.application.ITranslateLocalServiceProvider;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.IPresenterFactory;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.translate.ITranslatePresenter;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.translate.TranslatePresenter;
import com.danielkashin.yandextestapplication.presentation_layer.view.base.PresenterFragment;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;


public class TranslateFragment extends PresenterFragment<TranslatePresenter, ITranslateView>
    implements ITranslateView, IMainPage, IDatabaseChangePublisher, ITranslateHolder {

  private LinearLayout mRootView;
  private TextView mOriginalLanguageText;
  private TextView mTranslatedLanguageText;
  private ImageView mSwapLanguagesImage;
  private EditText mOriginalTextEdit;
  private ImageView mClearImage;
  private RelativeLayout mTranslationLayout;
  private TextView mTranslatedText;
  private RelativeLayout mProgressBarLayout;
  private RelativeLayout mNoInternetLayout;
  private TextWatcher mTextWatcher;
  private ToggleButton mToggleFavorite;

  private State mRestoredState;

  // ----------------------------------- getInstance ----------------------------------------------

  public static TranslateFragment getInstance() {
    return new TranslateFragment();
  }

  // ------------------------------------ lifecycle -----------------------------------------------

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (!(getActivity() instanceof IDatabaseChangePublisher)) {
      throw new IllegalStateException("Parent activity must implement IDatabaseChangePublisher");
    }

    // try to restore state from saved instance
    mRestoredState = new State(savedInstanceState);

    if (!mRestoredState.isLanguagePairInitialized()) {
      // could not restore state -- check arguments
      mRestoredState = new State(getArguments());
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
    return super.onCreateView(inflater, parent, savedInstanceState);
  }

  @Override
  public void onStart() {
    super.onStart();

    if (!mRestoredState.isLanguagePairInitialized()) {
      // activity state is not initialized -- call fragment to initialized
      getPresenter().onFirstStart();
    } else {
      // activity state is initialized -- presenter does something else
      getPresenter().onNotFirstStart();
    }

    setListeners();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    mRestoredState.setClearImageVisible(mClearImage.getVisibility() == View.VISIBLE);
    mRestoredState.setTranslationLayoutVisible(mTranslationLayout.getVisibility() == View.VISIBLE);
    mRestoredState.saveToOutState(outState);
  }

  // ------------------------------- IDatabaseChangeReceiver --------------------------------------

  @Override
  public void receiveOnDataChanged(IDatabaseChangeReceiver source) {
    getPresenter().onRefreshFavoriteValue(
        mOriginalTextEdit.getText().toString(),
        mTranslatedText.getText().toString(),
        mRestoredState.getLanguagePair().getLanguageCodePair(),
        mToggleFavorite.isChecked());
  }

  // ----------------------------------- IDatabaseNotifier ----------------------------------------

  @Override
  public void publishOnDataChanged(IDatabaseChangePublisher source) {
    ((IDatabaseChangePublisher) getActivity()).publishOnDataChanged(this);
  }

  // ----------------------------------- ITranslateHolder -----------------------------------------

  @Override
  public void setTranslationData(Translation translation) {
    if (getPresenter() != null) {
      getPresenter().onSetTranslationData(translation);
    }
  }

  // --------------------------------------- IMainPage --------------------------------------------

  @Override
  public void onAnotherPageSelected() {
    // TODO
  }

  @Override
  public void onSelected() {
    // TODO
  }


  // ------------------------------------ ITranslateView ------------------------------------------

  //                        ------------- data changed -----------------


  @Override
  public void onTranslationFavoriteChanged() {
    publishOnDataChanged(this);
  }

  @Override
  public void onTranslationSaved() {
    publishOnDataChanged(this);
  }

  //                        --------------- languages ------------------

  @Override
  public void setLanguages(LanguagePair languages) {
    // state handles exceptions by itself -- no need to check it here
    removeSwapLanguagesListener();
    mRestoredState.setLanguagePair(languages);
    mOriginalLanguageText.setText(languages.getOriginalLanguage().getText());
    mTranslatedLanguageText.setText(languages.getTranslatedLanguage().getText());
    setSwapLanguagesListener();
  }

  @Override
  public LanguagePair getLanguages() {
    return mRestoredState.getLanguagePair();
  }

  @Override
  public LanguagePair getLanguagesIfInitialized() {
    if (mRestoredState.isLanguagePairInitialized()) {
      return mRestoredState.getLanguagePair();
    } else {
      return null;
    }
  }

  @Override
  public void setOriginalLanguage(Language language) {
    mRestoredState.setOriginalLanguage(language);
    mOriginalLanguageText.setText(language.getText());
  }

  @Override
  public void setTranslatedLanguage(Language language) {
    mRestoredState.setTranslatedLanguage(language);
    mTranslatedLanguageText.setText(language.getText());
  }

  //                        --------------- listeners ------------------

  @Override
  public void setToggleFavoriteListener() {
    mToggleFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        getPresenter().onToggleCheckedChanged(mOriginalTextEdit.getText().toString(),
            mTranslatedText.getText().toString(),
            mRestoredState.getLanguagePair().getLanguageCodePair(),
            isChecked);
      }
    });
  }

  @Override
  public void removeToggleFavoriteListener() {
    mToggleFavorite.setOnCheckedChangeListener(null);
  }

  @Override
  public void setSwapLanguagesListener() {
    // set this listener only after initialization of languages was performed
    mSwapLanguagesImage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        swapLanguages();
        mOriginalTextEdit.setText(mTranslatedText.getText());
      }
    });
  }

  @Override
  public void removeSwapLanguagesListener() {
    // when presenter unbinds view
    mSwapLanguagesImage.setOnClickListener(null);
  }

  @Override
  public void setTextWatcher() {
    mTextWatcher = new TextWatcher() {
      // fields to perform delay
      private final static int INPUT_DELAY_IN_MS = 500;
      private Handler handler = new Handler(Looper.getMainLooper());
      Runnable workRunnable;

      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        handler.removeCallbacks(workRunnable);
      }

      @Override
      public void afterTextChanged(final Editable editable) {
        if (editable.toString().replace(" ", "").isEmpty()) {
          mClearImage.setVisibility(View.INVISIBLE);
          getPresenter().onInputTextClear();
        } else {
          mClearImage.setVisibility(View.VISIBLE);
          workRunnable = new Runnable() {
            @Override
            public void run() {
              getPresenter().onInputTextChanged(editable.toString());
            }
          };
          handler.postDelayed(workRunnable, INPUT_DELAY_IN_MS);
        }
      }
    };

    mOriginalTextEdit.addTextChangedListener(mTextWatcher);
  }

  @Override
  public void removeTextWatcher() {
    mOriginalTextEdit.removeTextChangedListener(mTextWatcher);
  }

  //                        ------------ hide/show views ---------------

  @Override
  public void hideNoInternet() {
    mNoInternetLayout.setVisibility(View.INVISIBLE);
  }

  @Override
  public void showNoInternet() {
    mNoInternetLayout.setVisibility(View.VISIBLE);
  }

  @Override
  public void showProgressBar() {
    mToggleFavorite.setEnabled(false);
    mTranslationLayout.setAlpha(0.5f);
    mProgressBarLayout.setVisibility(View.VISIBLE);
  }

  @Override
  public void hideProgressBar() {
    mProgressBarLayout.setVisibility(View.INVISIBLE);
    mTranslationLayout.setAlpha(1.0f);
    mToggleFavorite.setEnabled(true);
  }

  @Override
  public void showImageClear() {
    mClearImage.setVisibility(View.VISIBLE);
  }

  @Override
  public void hideImageClear() {
    mClearImage.setVisibility(View.INVISIBLE);
  }

  @Override
  public void hideTranslationLayout() {
    removeToggleFavoriteListener();
    mTranslationLayout.setVisibility(View.INVISIBLE);
    mTranslatedText.setText("");
  }

  //                        ----------- other view handling ------------

  @Override
  public void setToggleFavoriteValue(boolean favorite) {
    removeToggleFavoriteListener();
    mToggleFavorite.setChecked(favorite);
    setToggleFavoriteListener();
  }

  @Override
  public void setTranslation(Translation translation) {
    removeToggleFavoriteListener();
    removeTextWatcher();
    mOriginalTextEdit.setText(translation.getOriginalText());
    mTranslatedText.setText(translation.getTranslatedText());
    mToggleFavorite.setChecked(translation.ifFavorite());
    mTranslationLayout.setVisibility(View.VISIBLE);
    setTextWatcher();
    setToggleFavoriteListener();
  }

  @Override
  public void showAlertDialog(String text) {
    new AlertDialog.Builder(getContext())
        .setMessage(text)
        .create()
        .show();
  }

  @Override
  public String getStringById(int id) {
    return getResources().getString(id);
  }

  // ----------------------------------- PresenterFragment  ---------------------------------------

  @Override
  protected ITranslateView getViewInterface() {
    return this;
  }

  @Override
  protected IPresenterFactory<TranslatePresenter, ITranslateView> getPresenterFactory() {
    // bind services
    OkHttpClient okHttpClient = new OkHttpClient.Builder()
        .readTimeout(10, TimeUnit.SECONDS)
        .connectTimeout(10, TimeUnit.SECONDS)
        .build();
    ITranslationsRemoteService translateRemoteService = TranslationsRemoteService.Factory
        .create(okHttpClient);

    ITranslationsLocalService translateLocalService = ((ITranslateLocalServiceProvider) getActivity()
        .getApplication())
        .getTranslateLocalService();

    ILanguagesLocalService supportedLanguagesLocalService =
        LanguagesLocalService.Factory
            .create(getContext());


    // bind repositories
    ITranslationsRepository translateRepository = TranslationsRepository.Factory
        .create(translateLocalService, translateRemoteService);

    ILanguagesRepository supportedLanguagesRepository =
        LanguagesRepository.Factory
            .create(supportedLanguagesLocalService);


    // bind use cases
    TranslateUseCase translateUseCase = new TranslateUseCase(
        AsyncTask.THREAD_POOL_EXECUTOR,
        translateRepository);

    GetLastTranslationUseCase getLastTranslationUseCase = new GetLastTranslationUseCase(
        AsyncTask.THREAD_POOL_EXECUTOR,
        translateRepository,
        supportedLanguagesRepository);

    SaveTranslationUseCase setTranslationFavoriteUseCase = new SaveTranslationUseCase(
        AsyncTask.THREAD_POOL_EXECUTOR,
        translateRepository);

    GetRefreshedTranslationUseCase getRefreshedTranslationUseCase = new GetRefreshedTranslationUseCase(
        AsyncTask.THREAD_POOL_EXECUTOR,
        translateRepository);

    GetLanguagesFromTranslationUseCase getLanguagesFromTranslationUseCase =
        new GetLanguagesFromTranslationUseCase(AsyncTask.THREAD_POOL_EXECUTOR,
            supportedLanguagesRepository);


    // bind network manager
    INetworkManager networkManager = NetworkManager.Factory.create(getContext());


    // return presenter with dependencies
    return new TranslatePresenter.Factory(translateUseCase,
        getLastTranslationUseCase,
        setTranslationFavoriteUseCase,
        getRefreshedTranslationUseCase,
        getLanguagesFromTranslationUseCase,
        networkManager);
  }

  @Override
  protected int getFragmentId() {
    return TranslateFragment.class.getSimpleName().hashCode();
  }

  @Override
  protected int getLayoutRes() {
    return R.layout.fragment_translate;
  }

  @Override
  protected void initializeView(View view) {
    mRootView = (LinearLayout) view.findViewById(R.id.root_view);
    mOriginalLanguageText = (TextView) view.findViewById(R.id.text_original_language);
    mTranslatedLanguageText = (TextView) view.findViewById(R.id.text_translated_language);
    mSwapLanguagesImage = (ImageView) view.findViewById(R.id.image_swap_languages);
    mOriginalTextEdit = (EditText) view.findViewById(R.id.edit_original);
    mClearImage = (ImageView) view.findViewById(R.id.image_clear);
    mTranslationLayout = (RelativeLayout) view.findViewById(R.id.layout_translation);
    mTranslatedText = (TextView) view.findViewById(R.id.text_translated);
    mProgressBarLayout = (RelativeLayout) view.findViewById(R.id.layout_progress_bar);
    mNoInternetLayout = (RelativeLayout) view.findViewById(R.id.layout_no_internet);
    mToggleFavorite = (ToggleButton) view.findViewById(R.id.toggle_favorite);

    if (mRestoredState.isLanguagePairInitialized()) {
      mOriginalLanguageText.setText(mRestoredState.getLanguagePair().getOriginalLanguage().getText());
      mTranslatedLanguageText.setText(mRestoredState.getLanguagePair().getTranslatedLanguage().getText());
    }

    mClearImage.setVisibility(mRestoredState.isClearImageVisible() ? View.VISIBLE : View.INVISIBLE);
    mTranslationLayout.setVisibility(mRestoredState.isTranslationLayoutVisible() ? View.VISIBLE : View.INVISIBLE);
  }

  // --------------------------------------- private ----------------------------------------------

  private void setListeners() {
    mRootView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        mOriginalTextEdit.clearFocus();

        ((InputMethodManager) TranslateFragment.this
            .getContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE))
            .hideSoftInputFromWindow(view.getWindowToken(), 0);
      }
    });

    mClearImage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        mOriginalTextEdit.setText("");
      }
    });

    mTranslatedText.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        ClipboardManager manager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (manager != null) {
          manager.setPrimaryClip(ClipData.newPlainText("", mTranslatedText.getText().toString()));
          Toast.makeText(getContext(), getContext().getString(R.string.text_copied), Toast.LENGTH_SHORT).show();
        }
      }
    });
  }

  private void swapLanguages() {
    mRestoredState.getLanguagePair().swapLanguages();
    mOriginalLanguageText.setText(mRestoredState.getLanguagePair().getOriginalLanguage().getText());
    mTranslatedLanguageText.setText(mRestoredState.getLanguagePair().getTranslatedLanguage().getText());
  }

  // ------------------------------------ inner classes -------------------------------------------

  private class State {

    private static final String KEY_CLEAR_IMAGE_VISIBLE = "KEY_CLEAR_IMAGE_VISIBLE";
    private static final String KEY_TRANSLATION_LAYOUT_VISIBLE = "TRANSLATION_LAYOUT_VISIBLE";

    private LanguagePair languagePair;
    private boolean clearImageVisible;
    private boolean translationLayoutVisible;


    private State(Bundle bundle) {
      if (bundle == null) {
        languagePair = null;
        clearImageVisible = false;
        return;
      }

      try {
        languagePair = LanguagePair.Factory.create(bundle);
        clearImageVisible = bundle.containsKey(KEY_CLEAR_IMAGE_VISIBLE) && bundle.getBoolean(KEY_CLEAR_IMAGE_VISIBLE);
        translationLayoutVisible = bundle.containsKey(KEY_TRANSLATION_LAYOUT_VISIBLE) &&
            bundle.getBoolean(KEY_TRANSLATION_LAYOUT_VISIBLE);
      } catch (IllegalArgumentException e) {
        languagePair = null;
        clearImageVisible = false;
        translationLayoutVisible = false;
      }
    }

    private void saveToOutState(Bundle outState) {
      if (isLanguagePairInitialized()) {
        languagePair.saveToBundle(outState);
      }
      outState.putBoolean(KEY_CLEAR_IMAGE_VISIBLE, clearImageVisible);
      outState.putBoolean(KEY_TRANSLATION_LAYOUT_VISIBLE, translationLayoutVisible);
    }

    //                        ------------ setters --------------

    private void setLanguagePair(LanguagePair languagePair) {
      this.languagePair = languagePair;
    }

    private void setOriginalLanguage(Language language) {
      if (!isLanguagePairInitialized()) {
        throw new IllegalStateException("LanguagePair must be initialized when setting a language");
      }

      languagePair.setOriginalLanguage(language);
    }

    private void setTranslatedLanguage(Language language) {
      if (!isLanguagePairInitialized()) {
        throw new IllegalStateException("LanguagePair must be initialized when setting a language");
      }

      languagePair.setTranslatedLanguage(language);
    }

    private void setClearImageVisible(boolean clearImageVisible) {
      this.clearImageVisible = clearImageVisible;
    }

    private void setTranslationLayoutVisible(boolean translationLayoutVisible) {
      this.translationLayoutVisible = translationLayoutVisible;
    }

    //                        ------------ getters --------------

    private boolean isLanguagePairInitialized() {
      return languagePair != null;
    }

    private boolean isClearImageVisible() {
      return clearImageVisible;
    }

    private boolean isTranslationLayoutVisible() {
      return translationLayoutVisible;
    }

    private LanguagePair getLanguagePair() {
      if (!isLanguagePairInitialized()) {
        throw new IllegalStateException("Languages must me initialized when getting");
      }

      return languagePair;
    }
  }
}
