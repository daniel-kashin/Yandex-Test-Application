package com.danielkashin.yandextestapplication.presentation_layer.adapter.translations;


public interface ITranslationsCallbacks {

  void onFavoriteToggleClicked(int position);

  void onItemClicked(int position);

  void onLongItemClicked(int position);

}
