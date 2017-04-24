package com.danielkashin.yandextestapplication.presentation_layer.adapter.translations;

import android.os.Bundle;

import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;

import java.util.List;


public interface ITranslationsAdapter {

  void addCallbacks(Callbacks callbacks);

  void removeCallbacks();


  void addTranslations(List<Translation> translations, boolean clear);

  void deleteTranslation(Translation translation);

  void clear();

  int getSize();

  boolean isInitialized();

  boolean containsOnlyFavoriteTranslations();

  boolean isDataUploadingToEndNeeded(int lastVisibleItem);

  void setEndReachedTrue();

  void setDataUploadingToEndTrue();


  void onSaveInstanceState(Bundle outState);


  interface Callbacks {

    void onToggleFavoriteClicked(Translation translation);

    void onItemClicked(Translation translation);

    void onLongItemClicked(Translation translation);

  }
}
