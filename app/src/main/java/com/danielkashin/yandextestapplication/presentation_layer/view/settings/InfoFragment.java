package com.danielkashin.yandextestapplication.presentation_layer.view.settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.danielkashin.yandextestapplication.R;


public class InfoFragment extends Fragment {

  // ---------------------------------- getInstance -----------------------------------------------

  public static InfoFragment getInstance() {
    return new InfoFragment();
  }

  // ----------------------------------- lifecycle ------------------------------------------------

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
    super.onCreateView(inflater, parent, savedInstanceState);
    return inflater.inflate(R.layout.fragment_info, parent, false);
  }

}
