package com.danielkashin.yandextestapplication.data_layer.services.local;


import com.danielkashin.yandextestapplication.data_layer.entities.local.DatabaseTranslation;
import com.danielkashin.yandextestapplication.data_layer.services.base.DatabaseService;

import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmResults;


public class TranslateDatabaseService
    extends DatabaseService implements ITranslateDatabaseService {

  private TranslateDatabaseService() {
    super();
  }


  @Override
  public DatabaseTranslation getLastTranslation() {
    return getService().where(DatabaseTranslation.class)
        .findAll()
        .last();
  }

  @Override
  public RealmResults<DatabaseTranslation> getAllTranslationsAsync() {
    return getService()
        .where(DatabaseTranslation.class)
        .findAllAsync();
  }

  @Override
  public RealmResults<DatabaseTranslation> getFavouriteTranslationsAsync() {
    return getService().where(DatabaseTranslation.class)
        .equalTo("isFavourite", true)
        .findAllAsync();
  }

  @Override
  public void deleteAllTranslations() {
    try { // I could use try-with-resources here
      getService().executeTransaction(new Realm.Transaction() {
        @Override
        public void execute(Realm realm) {
          realm.delete(DatabaseTranslation.class);
        }
      });
    } finally {
        getService().close();
    }
  }

  @Override
  public void deleteFavouriteTranslations() {
    RealmResults<DatabaseTranslation> translations =
        getService().where(DatabaseTranslation.class)
        .findAll();

    for (DatabaseTranslation translation : translations){
      translation.setIsFavourite(false);
    }
  }

  @Override
  public RealmAsyncTask saveTranslationAsync(final String originalText, final String translatedText,
                                             final String language, Realm.Transaction.OnError onError) {
    return getService().executeTransactionAsync(new Realm.Transaction() {
        @Override
        public void execute(Realm realm) {
          DatabaseTranslation translation = realm.createObject(DatabaseTranslation.class);
          translation.setOriginalText(originalText);
          translation.setLanguage(language);
          translation.setTranslatedText(translatedText);
          translation.setIsFavourite(false);
        }
    }, onError);
  }


  public static class Factory {

    private Factory() {}

    public static ITranslateDatabaseService create() {
      return new TranslateDatabaseService();
    }

  }
}
