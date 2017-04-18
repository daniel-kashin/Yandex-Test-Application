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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.danielkashin.yandextestapplication.R;
import com.danielkashin.yandextestapplication.data_layer.managers.network.INetworkManager;
import com.danielkashin.yandextestapplication.data_layer.managers.network.NetworkManager;
import com.danielkashin.yandextestapplication.data_layer.services.supported_languages.local.ISupportedLanguagesLocalService;
import com.danielkashin.yandextestapplication.data_layer.services.supported_languages.local.SupportedLanguagesLocalService;
import com.danielkashin.yandextestapplication.data_layer.services.translate.local.ITranslateLocalService;
import com.danielkashin.yandextestapplication.data_layer.services.translate.remote.ITranslateRemoteService;
import com.danielkashin.yandextestapplication.data_layer.services.translate.remote.TranslateRemoteService;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Language;
import com.danielkashin.yandextestapplication.domain_layer.pojo.LanguagePair;
import com.danielkashin.yandextestapplication.domain_layer.repository.supported_languages.ISupportedLanguagesRepository;
import com.danielkashin.yandextestapplication.domain_layer.repository.supported_languages.SupportedLanguagesRepository;
import com.danielkashin.yandextestapplication.domain_layer.repository.translate.ITranslateRepository;
import com.danielkashin.yandextestapplication.domain_layer.repository.translate.TranslateRepository;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.GetLastTranslationUseCase;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.TranslateUseCase;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.base.IDatabaseChangeReceiver;
import com.danielkashin.yandextestapplication.presentation_layer.application.ITranslateLocalServiceProvider;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.IPresenterFactory;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.translate.TranslatePresenter;
import com.danielkashin.yandextestapplication.presentation_layer.view.base.PresenterFragment;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;


public class TranslateFragment extends PresenterFragment<TranslatePresenter, ITranslateView>
    implements ITranslateView, IDatabaseChangeReceiver {

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

  private State mRestoredState;

  // ----------------------------------- getInstance ----------------------------------------------

  public static TranslateFragment getInstance() {
    return new TranslateFragment();
  }

  // ------------------------------------ lifecycle -----------------------------------------------

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (!(getActivity() instanceof IDatabaseChangeReceiver)) {
      throw new IllegalStateException("Parent activity must implement IDatabaseChangeReceiver");
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
    mRestoredState.saveToOutState(outState);
  }

  // ------------------------------- IDatabaseChangeReceiver --------------------------------------

  @Override
  public void receiveOnDataChanged(IDatabaseChangeReceiver source) {

  }

  // ---------------------------------- IDatabaseNotifier -----------------------------------------

  @Override
  public void publishOnDataChanged() {
    ((IDatabaseChangeReceiver)getActivity()).receiveOnDataChanged(this);
  }

  // ------------------------------------ ITranslateView ------------------------------------------

  //                        --------------- languages ------------------

  @Override
  public void initializeLanguages(LanguagePair languages) {
    // state handles exceptions by itself -- no need to check it here
    mRestoredState.setLanguagePair(languages);
    mOriginalLanguageText.setText(languages.getOriginalLanguage().getText());
    mTranslatedLanguageText.setText(languages.getTranslatedLanguage().getText());
  }

  @Override
  public LanguagePair getLanguages() {
    return mRestoredState.getLanguagePair();
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
    mProgressBarLayout.setVisibility(View.VISIBLE);
  }

  @Override
  public void hideProgressBar() {
    mProgressBarLayout.setVisibility(View.INVISIBLE);
  }

  @Override
  public void showImageClear() {
    mClearImage.setVisibility(View.VISIBLE);
  }

  @Override
  public void hideImageClear() {
    mClearImage.setVisibility(View.INVISIBLE);
  }

  //                        ----------- other view handling ------------

  @Override
  public void setInputText(String text) {
    mOriginalTextEdit.setText(text);
  }

  @Override
  public void setTranslatedText(String text) {
    mTranslatedText.setText(text);
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
    ITranslateRemoteService translateRemoteService = TranslateRemoteService.Factory
        .create(okHttpClient);

    ITranslateLocalService translateLocalService = ((ITranslateLocalServiceProvider) getActivity()
        .getApplication())
        .getTranslateLocalService();

    ISupportedLanguagesLocalService supportedLanguagesLocalService =
        SupportedLanguagesLocalService.Factory
            .create(getContext());


    // bind repositories
    ITranslateRepository translateRepository = TranslateRepository.Factory
        .create(translateLocalService, translateRemoteService);

    ISupportedLanguagesRepository supportedLanguagesRepository =
        SupportedLanguagesRepository.Factory
            .create(supportedLanguagesLocalService);


    // bind use cases
    TranslateUseCase translateUseCase = new TranslateUseCase(
        AsyncTask.THREAD_POOL_EXECUTOR,
        translateRepository);

    GetLastTranslationUseCase getLastTranslationUseCase = new GetLastTranslationUseCase(
        AsyncTask.THREAD_POOL_EXECUTOR,
        translateRepository,
        supportedLanguagesRepository);


    // bind network manager
    INetworkManager networkManager = NetworkManager.Factory.create(getContext());


    // return presenter with dependencies
    return new TranslatePresenter.Factory(translateUseCase, getLastTranslationUseCase, networkManager);
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
    mOriginalLanguageText = (TextView) view.findViewById(R.id.text_original_language);
    mTranslatedLanguageText = (TextView) view.findViewById(R.id.text_translated_language);
    mSwapLanguagesImage = (ImageView) view.findViewById(R.id.image_swap_languages);
    mOriginalTextEdit = (EditText) view.findViewById(R.id.edit_original);
    mClearImage = (ImageView) view.findViewById(R.id.image_clear);
    mTranslationLayout = (RelativeLayout) view.findViewById(R.id.layout_translation);
    mTranslatedText = (TextView) view.findViewById(R.id.text_translated);
    mProgressBarLayout = (RelativeLayout) view.findViewById(R.id.layout_progress_bar);
    mNoInternetLayout = (RelativeLayout) view.findViewById(R.id.layout_no_internet);

    if (mRestoredState.isLanguagePairInitialized()) {
      mOriginalLanguageText.setText(mRestoredState.getLanguagePair().getOriginalLanguage().getText());
      mTranslatedLanguageText.setText(mRestoredState.getLanguagePair().getTranslatedLanguage().getText());
    }

    mClearImage.setVisibility(mRestoredState.clearImageVisible ? View.VISIBLE : View.INVISIBLE);
  }

  // --------------------------------------- private ----------------------------------------------

  private void setListeners() {
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

    private LanguagePair languagePair;
    private boolean clearImageVisible;


    private State(Bundle bundle) {
      if (bundle == null){
        languagePair = null;
        clearImageVisible = false;
        return;
      }

      try {
        languagePair = LanguagePair.Factory.create(bundle);
        clearImageVisible = bundle.containsKey(KEY_CLEAR_IMAGE_VISIBLE)
            && bundle.getBoolean(KEY_CLEAR_IMAGE_VISIBLE);
      } catch (IllegalArgumentException e) {
        languagePair = null;
        clearImageVisible = false;
      }
    }

    private void saveToOutState(Bundle outState) {
      if (isLanguagePairInitialized()) {
        languagePair.saveToBundle(outState);
      }
      outState.putBoolean(KEY_CLEAR_IMAGE_VISIBLE, clearImageVisible);
    }

    //                        ------------ setters --------------

    private void setLanguagePair(LanguagePair languagePair){
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

    //                        ------------ getters --------------

    private boolean isLanguagePairInitialized() {
      return languagePair != null;
    }

    private boolean isClearImageVisible() {
      return clearImageVisible;
    }

    private LanguagePair getLanguagePair() {
      if (!isLanguagePairInitialized()) {
        throw new IllegalStateException("Languages must me initialized when getting");
      }

      return languagePair;
    }
  }
}
