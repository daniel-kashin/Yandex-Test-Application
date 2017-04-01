package com.danielkashin.yandextestapplication.data_layer.services.local;

import com.danielkashin.yandextestapplication.data_layer.entities.local.Translation;
import com.danielkashin.yandextestapplication.data_layer.exceptions.EmptyTranslationExceptionBundle;
import com.danielkashin.yandextestapplication.data_layer.exceptions.EmptyTranslationsExceptionBundle;

import io.realm.RealmResults;


public interface IRealmTranslateDatabaseService {

  RealmResults<Translation> getAllTranslations() throws EmptyTranslationsExceptionBundle;

  RealmResults<Translation> getFavouriteTranslations() throws EmptyTranslationsExceptionBundle;

  void deleteAllTranslations();

  void deleteFavouriteTranslations();

  void addTranslation(String originalText, String translatedText, String language);

  void toggleTranslationFavourite(String originalName, String language) throws EmptyTranslationExceptionBundle;

}
