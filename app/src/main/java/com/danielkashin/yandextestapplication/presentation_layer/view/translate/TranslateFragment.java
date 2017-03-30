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

  private EditText mEditTranslate;
  private ImageView mImageClear;
  private TextView mTextResult;
  private ProgressBar mProgressBar;


  public static TranslateFragment getInstance() {
    return new TranslateFragment();
  }

  // ---------------------------------- ITranslateView methods ------------------------------------

  @Override
  public void setResultText(String text) {
    mTextResult.setText(text);
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
    mTextResult.setTextColor(ContextCompat.getColor(getContext(), R.color.light_grey));
    mProgressBar.setVisibility(View.VISIBLE);
  }

  @Override
  public void hideProgressBar() {
    mTextResult.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
    mProgressBar.setVisibility(View.GONE);
  }

  // -------------------------------- PresenterFragment methods -----------------------------------

  @Override
  protected ITranslateView getViewInterface() {
    return this;
  }

  @Override
  protected IPresenterFactory<TranslatePresenter, ITranslateView> getPresenterFactory() {
    return new TranslatePresenter.TranslateFactory();
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
    mEditTranslate = (EditText) view.findViewById(R.id.edit_translate);
    mImageClear = (ImageView) view.findViewById(R.id.image_clear);
    mTextResult = (TextView) view.findViewById(R.id.text_result);
    mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
  }

  @Override
  protected void setListeners() {
    mImageClear.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        mEditTranslate.setText("");
      }
    });

    mEditTranslate.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

      @Override
      public void afterTextChanged(Editable editable) {
        if (editable.toString().replace(" ", "").isEmpty()) {
          getPresenter().onInputTextClear();
        } else {
          getPresenter().onInputTextChanged(editable.toString());
        }
      }
    });
  }
}
