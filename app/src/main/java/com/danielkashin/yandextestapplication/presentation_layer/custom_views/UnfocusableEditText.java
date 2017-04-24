package com.danielkashin.yandextestapplication.presentation_layer.custom_views;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.KeyEvent;


public class UnfocusableEditText extends AppCompatEditText {

  public UnfocusableEditText(Context context){
    super(context);
  }

  public UnfocusableEditText(Context context, AttributeSet attrs){
    super(context, attrs);
  }

  @Override
  public boolean onKeyPreIme(int keyCode, KeyEvent event) {
    if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
      // lose focus when soft keyboard is hidden
      this.clearFocus();
    }

    return super.onKeyPreIme(keyCode, event);
  }

}
