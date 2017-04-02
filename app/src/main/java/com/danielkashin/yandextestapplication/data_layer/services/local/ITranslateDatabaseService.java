package com.danielkashin.yandextestapplication.data_layer.services.local;

import com.danielkashin.yandextestapplication.data_layer.entities.local.DatabaseTranslation;

import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public interface ITranslateDatabaseService {

  DatabaseTranslation getLastTranslation();

  RealmResults<DatabaseTranslation> getAllTranslationsAsync();

  RealmResults<DatabaseTranslation> getFavouriteTranslationsAsync();

  void deleteAllTranslations();

  void deleteFavouriteTranslations();

  RealmAsyncTask saveTranslationAsync(String originalText, String translatedText,
                                      String language, Realm.Transaction.OnError onError);

}
