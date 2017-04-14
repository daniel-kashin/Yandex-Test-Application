package com.danielkashin.yandextestapplication.presentation_layer.view.translate;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentManager;
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

import com.danielkashin.yandextestapplication.R;
import com.danielkashin.yandextestapplication.data_layer.managers.network.INetworkManager;
import com.danielkashin.yandextestapplication.data_layer.managers.network.NetworkManager;
import com.danielkashin.yandextestapplication.data_layer.services.translation.local.ITranslationLocalService;
import com.danielkashin.yandextestapplication.data_layer.services.translation.remote.ITranslationRemoteService;
import com.danielkashin.yandextestapplication.data_layer.services.translation.remote.TranslationRemoteService;
import com.danielkashin.yandextestapplication.domain_layer.repository.ITranslationRepository;
import com.danielkashin.yandextestapplication.domain_layer.repository.TranslationRepository;
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

  private EditText mEditOriginal;
  private ImageView mImageClear;
  private TextView mTextTranslated;
  private RelativeLayout mProgressBarLayout;
  private RelativeLayout mTranslationLayout;
  private RelativeLayout mNoInternetLayout;
  private TextWatcher mTextWatcher;
  private State mState;

  // ---------------------------------- getInstance methods ---------------------------------------

  public static TranslateFragment getInstance() {
    TranslateFragment translateFragment = new TranslateFragment();

    return translateFragment;
  }

  // ------------------------------------ lifecycle -----------------------------------------------

  @Override
  public void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);

    if (getArguments() != null){
      mState = State.INITIALIZED_FROM_ARGUMENTS;
    } else if (savedInstanceState != null){
      mState = State.INITIALIZED_FROM_BUNDLE;
    } else {
      mState = State.NOT_INITIALIZED;
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
    return super.onCreateView(inflater, parent, savedInstanceState);
  }

  @Override
  public void onStart(){
    super.onStart();

    setListeners();

    if (mState == State.NOT_INITIALIZED){
      mState = State.INITIALIZED_FROM_PRESENTER;
      getPresenter().onDataWasNotRestored();
    } else {
      setTextWatcher();
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState){
    super.onSaveInstanceState(outState);
  }

  // ---------------------------------- ITranslateView methods ------------------------------------

  @Override
  public void setTextWatcher() {
    mTextWatcher = new TextWatcher() {
      private final static int INPUT_DELAY_IN_MS = 500;
      private Handler handler = new Handler(Looper.getMainLooper());
      Runnable workRunnable;

      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        handler.removeCallbacks(workRunnable);
      }

      @Override
      public void afterTextChanged(final Editable editable) {
        if (editable.toString().replace(" ", "").isEmpty()) {
          getPresenter().onInputTextClear();
        } else {
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

    mEditOriginal.addTextChangedListener(mTextWatcher);
  }

  @Override
  public void removeTextWatcher() {
    mEditOriginal.removeTextChangedListener(mTextWatcher);
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
  public void setInputText(String text) {
    mEditOriginal.setText(text);
  }

  @Override
  public void setTranslatedText(String text) {
    mTextTranslated.setText(text);
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

  // -------------------------------- PresenterFragment methods -----------------------------------

  @Override
  protected ITranslateView getViewInterface() {
    return this;
  }

  @Override
  protected IPresenterFactory<TranslatePresenter, ITranslateView> getPresenterFactory() {
    // bind remote service
    ITranslationRemoteService remoteService = TranslationRemoteService.Factory.create(
            new OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build());

    // bind local service
    ITranslationLocalService localService = ((ITranslateLocalServiceProvider)getActivity()
        .getApplication())
        .getTranslateLocalService();

    // bind repository
    ITranslationRepository repository = TranslationRepository.Factory.create(localService, remoteService);

    // bind use cases
    TranslateUseCase translateUseCase = new TranslateUseCase(AsyncTask.THREAD_POOL_EXECUTOR, repository);

    GetLastTranslationUseCase getLastTranslationUseCase =
        new GetLastTranslationUseCase(AsyncTask.THREAD_POOL_EXECUTOR, repository);

    // bind networkmanager
    INetworkManager networkManager = NetworkManager.Factory.create(getContext());

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
    mEditOriginal = (EditText) view.findViewById(R.id.edit_original);
    mImageClear = (ImageView) view.findViewById(R.id.image_clear);
    mTextTranslated = (TextView) view.findViewById(R.id.text_translated);
    mProgressBarLayout = (RelativeLayout) view.findViewById(R.id.layout_progress_bar);
    mTranslationLayout = (RelativeLayout) view.findViewById(R.id.layout_translation);
    mNoInternetLayout = (RelativeLayout) view.findViewById(R.id.layout_no_internet);
  }


  // ----------------------------------- private methods -----------------------------------------

  private void setListeners() {
    mImageClear.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        mEditOriginal.setText("");
      }
    });
  }

  // ------------------------------------ inner classes -------------------------------------------

  private enum State {
    INITIALIZED_FROM_ARGUMENTS,
    INITIALIZED_FROM_BUNDLE,
    INITIALIZED_FROM_PRESENTER,
    NOT_INITIALIZED
  }

}
