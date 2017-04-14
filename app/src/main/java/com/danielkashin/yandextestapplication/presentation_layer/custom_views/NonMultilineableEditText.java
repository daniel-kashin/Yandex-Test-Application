package com.danielkashin.yandextestapplication.presentation_layer.custom_views;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.KeyEvent;


public class NonMultilineableEditText extends AppCompatEditText {

  public NonMultilineableEditText(Context context){
    super(context);
  }

  public NonMultilineableEditText(Context context, AttributeSet attrs){
    super(context, attrs);
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent keyEvent){
    if (keyCode == KeyEvent.KEYCODE_ENTER){
      return true;
    }

    return super.onKeyDown(keyCode, keyEvent);
  }
}
