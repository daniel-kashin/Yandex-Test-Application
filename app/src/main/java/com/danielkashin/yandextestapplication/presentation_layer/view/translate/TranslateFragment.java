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
import com.danielkashin.yandextestapplication.data_layer.entities.supported_languages.local.Language;
import com.danielkashin.yandextestapplication.data_layer.managers.network.INetworkManager;
import com.danielkashin.yandextestapplication.data_layer.managers.network.NetworkManager;
import com.danielkashin.yandextestapplication.data_layer.services.supported_languages.local.ISupportedLanguagesLocalService;
import com.danielkashin.yandextestapplication.data_layer.services.supported_languages.local.SupportedLanguagesLocalService;
import com.danielkashin.yandextestapplication.data_layer.services.translate.local.ITranslateLocalService;
import com.danielkashin.yandextestapplication.data_layer.services.translate.remote.ITranslateRemoteService;
import com.danielkashin.yandextestapplication.data_layer.services.translate.remote.TranslateRemoteService;
import com.danielkashin.yandextestapplication.domain_layer.pojo.LanguagePair;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.danielkashin.yandextestapplication.domain_layer.repository.supported_languages.ISupportedLanguagesRepository;
import com.danielkashin.yandextestapplication.domain_layer.repository.supported_languages.SupportedLanguagesRepository;
import com.danielkashin.yandextestapplication.domain_layer.repository.translate.ITranslateRepository;
import com.danielkashin.yandextestapplication.domain_layer.repository.translate.TranslateRepository;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.GetLastTranslationUseCase;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.TranslateUseCase;
import com.danielkashin.yandextestapplication.presentation_layer.adapter.main_pager.ITranslationKeeper;
import com.danielkashin.yandextestapplication.presentation_layer.application.ITranslateLocalServiceProvider;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.IPresenterFactory;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.translate.TranslatePresenter;
import com.danielkashin.yandextestapplication.presentation_layer.view.base.PresenterFragment;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;


public class TranslateFragment extends PresenterFragment<TranslatePresenter, ITranslateView>
    implements ITranslateView, ITranslationKeeper {

  private TextView mOriginalLanguageText;
  private TextView mTranslatedLanguageText;
  private ImageView mChangeLanguagesImage;
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
    TranslateFragment translateFragment = new TranslateFragment();

    return translateFragment;
  }

  // ------------------------------------ lifecycle -----------------------------------------------

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mRestoredState = new State(savedInstanceState);

    if (!mRestoredState.isLanguagesInitialized()){
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

    setListeners();

    if (!mRestoredState.isLanguagesInitialized()) {
      getPresenter().onFirstStart();
    } else {
      setTextWatcher();
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    mRestoredState.saveToOutState(outState);
  }

  // ------------------------------------ ITranslateView  -----------------------------------------


  @Override
  public void swapLanguages() {
    if (mRestoredState.isLanguagesInitialized()) {
      mRestoredState.getLanguages().swapLanguages();
      mOriginalLanguageText.setText(mRestoredState.getLanguages().getOriginalLanguage().getText());
      mTranslatedLanguageText.setText(mRestoredState.getLanguages().getTranslatedLanguage().getText());
    }
  }

  @Override
  public void initializeLanguages(LanguagePair languages) {
    mRestoredState = new State(languages);
    mOriginalLanguageText.setText(languages.getOriginalLanguage().getText());
    mTranslatedLanguageText.setText(languages.getTranslatedLanguage().getText());
  }

  @Override
  public LanguagePair getLanguages() {
    return mRestoredState.getLanguages();
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

  @Override
  public void setTextWatcher() {
    mTextWatcher = new TextWatcher() {
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
          getPresenter().onInputTextClear();
          mClearImage.setVisibility(View.INVISIBLE);
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

  @Override
  public void setInputText(String text) {
    mOriginalTextEdit.setText(text);
  }

  @Override
  public void setTranslatedText(String text) {
    mTranslatedText.setText(text);
  }

  @Override
  public String getTranslatedText() {
    return mTranslatedText.getText().toString();
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
    mChangeLanguagesImage = (ImageView) view.findViewById(R.id.image_change_languages);
    mOriginalTextEdit = (EditText) view.findViewById(R.id.edit_original);
    mClearImage = (ImageView) view.findViewById(R.id.image_clear);
    mTranslationLayout = (RelativeLayout) view.findViewById(R.id.layout_translation);
    mTranslatedText = (TextView) view.findViewById(R.id.text_translated);
    mProgressBarLayout = (RelativeLayout) view.findViewById(R.id.layout_progress_bar);
    mNoInternetLayout = (RelativeLayout) view.findViewById(R.id.layout_no_internet);

    if (!mTranslatedText.getText().toString().equals("")) {
      mClearImage.setVisibility(View.VISIBLE);
    } else {
      mClearImage.setVisibility(View.INVISIBLE);
    }

    if (mRestoredState.isLanguagesInitialized()){
      mOriginalLanguageText.setText(mRestoredState.getLanguages().getOriginalLanguage().getText());
      mTranslatedLanguageText.setText(mRestoredState.getLanguages().getTranslatedLanguage().getText());
    }
  }

  // --------------------------------------- private ----------------------------------------------

  private void setListeners() {
    mClearImage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        mOriginalTextEdit.setText("");
      }
    });

    mChangeLanguagesImage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        getPresenter().onChangeButtonsImageClicked();
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

  // ------------------------------------ inner classes -------------------------------------------

  private class State {

    private LanguagePair languages;
    private boolean languagesInitialized;


    public State(Bundle bundle) {
      try {
        languages = new LanguagePair(bundle);
        languagesInitialized = true;
      } catch (IllegalStateException e) {
        languagesInitialized = false;
      }
    }

    public State(LanguagePair languages){
      if (languages != null) {
        this.languages = languages;
        languagesInitialized = true;
      } else {
        languagesInitialized = false;
      }
    }

    void saveToOutState(Bundle outState){
      if (languages != null) {
        languages.saveToBundle(outState);
      }
    }

    public LanguagePair getLanguages(){
      return languages;
    }

    public boolean isLanguagesInitialized() {
      return languagesInitialized;
    }

    public void setOriginalLanguage(Language language) {
      languages.setOriginalLanguage(language);
    }

    public void setTranslatedLanguage(Language language) {
      languages.setTranslatedLanguage(language);
    }

  }
}
