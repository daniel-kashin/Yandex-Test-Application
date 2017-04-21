package com.danielkashin.yandextestapplication.presentation_layer.adapter.translations;


import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;

public interface ITranslationsModelCallbacks {

  void onFavoriteToggleClicked(Translation translation);

  void onItemClicked(Translation translation);

  void onLongItemClicked(Translation translation);

}
