package com.danielkashin.yandextestapplication.presentation_layer.adapter.languages;

import android.os.Bundle;

import com.danielkashin.yandextestapplication.domain_layer.pojo.Language;

import java.util.ArrayList;


public interface ILanguagesAdapter {

  void addCallbacks(Callbacks callbacks);

  void removeCallbacks();


  void addLanguages(ArrayList<Language> languages);

  boolean isInitialized();


  void onSaveInstanceState(Bundle outState);


  interface Callbacks {

    void onItemClicked(Language language);

  }
}
