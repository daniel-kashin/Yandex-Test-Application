package com.danielkashin.yandextestapplication.presentation_layer.adapter.translations;

import android.os.Bundle;

import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;

import java.util.List;


public interface ITranslationsAdapter {

  void addCallbacks(ITranslationsAdapter.Callbacks callbacks);

  void removeCallbacks();

  void addTranslations(List<Translation> translations, boolean clear);

  void clear();

  int getSize();

  void onSaveInstanceState(Bundle outState);

  boolean isInitialized();

  boolean isDataUploadingToEndNeeded(int lastVisibleItem);

  void setEndReached();

  void deleteTranslation(Translation translation);

  void setDataUploadingToEndTrue();

  boolean isOnlyFavorite();


  interface Callbacks {

    void onToggleFavoriteClicked(Translation translation);

    void onItemClicked(Translation translation);

    void onLongItemClicked(Translation translation);

  }
}
