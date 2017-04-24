package com.danielkashin.yandextestapplication.presentation_layer.view.pick_language;

import com.danielkashin.yandextestapplication.domain_layer.pojo.Language;
import com.danielkashin.yandextestapplication.presentation_layer.view.base.IView;

import java.util.ArrayList;


public interface IPickLanguageView extends IView {

  void setAdapterCallbacks();

  void removeAdapterCallbacks();

  void addLanguagesToAdapter(ArrayList<Language> languages);

}
