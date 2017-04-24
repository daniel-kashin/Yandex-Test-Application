package com.danielkashin.yandextestapplication.presentation_layer.view.pick_language_holder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.danielkashin.yandextestapplication.R;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Language;
import com.danielkashin.yandextestapplication.presentation_layer.contracts.PickLanguageContract;

import static com.danielkashin.yandextestapplication.presentation_layer.contracts.PickLanguageContract.PickLanguageType;

public class PickLanguageHolderActivity extends AppCompatActivity
    implements IPickLanguageHolderView {

  private ImageView mImageToolbar;
  private TextView mTextToolbar;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (!getIntent().hasExtra(PickLanguageContract.KEY_PICK_LANGUAGE_TYPE)) {
      throw new IllegalStateException("Intent for PickLanguageHolderActivity must contain KEY_PICK_LANGUAGE_TYPE");
    }

    setContentView(R.layout.activity_pick_language_holder);
  }

  @Override
  protected void onStart() {
    super.onStart();

    initializeView();
  }

  private void initializeView() {
    mTextToolbar = (TextView) findViewById(R.id.text_toolbar);
    mImageToolbar = (ImageView) findViewById(R.id.image_toolbar);

    PickLanguageType pickLanguageType = (PickLanguageType) getIntent()
        .getSerializableExtra(PickLanguageContract.KEY_PICK_LANGUAGE_TYPE);
    if (pickLanguageType == PickLanguageType.PICK_TRANSLATED_LANGUAGE) {
      mTextToolbar.setText(getString(R.string.translated_language_label));
    } else {
      mTextToolbar.setText(getString(R.string.original_language_label));
    }

    mImageToolbar.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });
  }

  // -------------------------------- IPickLanguageHolderView -------------------------------------

  @Override
  public void onLanguageChosen(Language language) {
    PickLanguageType pickLanguageType = (PickLanguageType) getIntent()
        .getSerializableExtra(PickLanguageContract.KEY_PICK_LANGUAGE_TYPE);

    Intent intent = new Intent();
    intent.putExtra(PickLanguageContract.KEY_PICKED_TRANSLATION, language);
    intent.putExtra(PickLanguageContract.KEY_PICK_LANGUAGE_TYPE, pickLanguageType);
    setResult(RESULT_OK, intent);
    finish();
  }
}
