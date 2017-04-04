package com.danielkashin.yandextestapplication.data_layer.services.local;

import com.danielkashin.yandextestapplication.data_layer.entities.local.DatabaseTranslation;
import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;

import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmResults;


public interface ITranslateLocalService {

  DatabaseTranslation getLastTranslation() throws ExceptionBundle;

  RealmResults<DatabaseTranslation> getAllTranslationsAsync() throws ExceptionBundle;

  RealmResults<DatabaseTranslation> getFavouriteTranslationsAsync() throws ExceptionBundle;

  void deleteAllTranslations() throws ExceptionBundle;

  void deleteFavouriteTranslations() throws ExceptionBundle;

  RealmAsyncTask saveTranslationAsync(String originalText, String translatedText,
                                      String language, Realm.Transaction.OnError onError)
      throws ExceptionBundle;

}
