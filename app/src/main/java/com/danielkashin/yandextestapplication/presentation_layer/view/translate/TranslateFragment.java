package com.danielkashin.yandextestapplication.presentation_layer.view.translate;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.danielkashin.yandextestapplication.R;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.IPresenterFactory;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.translate.TranslatePresenter;
import com.danielkashin.yandextestapplication.presentation_layer.view.base.PresenterFragment;


public class TranslateFragment extends PresenterFragment<TranslatePresenter, ITranslateView>
    implements ITranslateView {

  private EditText mEditOriginal;
  private ImageView mImageClear;
  private TextView mTextTranslated;
  private ProgressBar mProgressBar;


  public static TranslateFragment getInstance() {
    return new TranslateFragment();
  }


  // ---------------------------------- ITranslateView methods ------------------------------------


  @Override
  public void removeInputTextListener(TextWatcher textWatcher) {
    mEditOriginal.removeTextChangedListener(textWatcher);
  }

  @Override
  public void setInputTextListener(TextWatcher textWatcher) {
    mEditOriginal.addTextChangedListener(textWatcher);
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

  @Override
  public void showProgressBar() {
    mTextTranslated.setTextColor(ContextCompat.getColor(getContext(), R.color.light_grey));
    mProgressBar.setVisibility(View.VISIBLE);
  }

  @Override
  public void hideProgressBar() {
    mTextTranslated.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
    mProgressBar.setVisibility(View.GONE);
  }

  // -------------------------------- PresenterFragment methods -----------------------------------

  @Override
  protected ITranslateView getViewInterface() {
    return this;
  }

  @Override
  protected IPresenterFactory<TranslatePresenter, ITranslateView> getPresenterFactory() {
    return new TranslatePresenter.Factory();
  }

  @Override
  protected int getFragmentId() {
    return "TranslateFragment".hashCode();
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
    mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
  }

  @Override
  protected void setListeners() {
    mImageClear.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        mEditOriginal.setText("");
      }
    });
  }
}
