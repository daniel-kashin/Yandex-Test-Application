package com.danielkashin.yandextestapplication.data_layer.services.local;


import android.content.res.Resources;

import com.danielkashin.yandextestapplication.data_layer.entities.local.Translation;
import com.danielkashin.yandextestapplication.data_layer.exceptions.EmptyTranslationExceptionBundle;
import com.danielkashin.yandextestapplication.data_layer.exceptions.EmptyTranslationsExceptionBundle;
import com.danielkashin.yandextestapplication.data_layer.services.base.DatabaseService;
import com.danielkashin.yandextestapplication.data_layer.services.remote.YandexTranslateNetworkService;

import io.realm.Realm;
import io.realm.RealmResults;


public class RealmTranslateDatabaseService
    extends DatabaseService implements IRealmTranslateDatabaseService {

  private RealmTranslateDatabaseService() {
    super();
  }


  @Override
  public RealmResults<Translation> getAllTranslations()
      throws EmptyTranslationsExceptionBundle {

    RealmResults<Translation> translations =  getService()
        .where(Translation.class)
        .findAll();

    if (translations.isEmpty()) throw new Resources.NotFoundException("Translations are empty");

    return translations;
  }

  @Override
  public RealmResults<Translation> getFavouriteTranslations()
      throws EmptyTranslationsExceptionBundle {

    return getService().where(Translation.class)
        .equalTo("isFavourite", true)
        .findAll();
  }

  @Override
  public void deleteAllTranslations() {
    try { // I could use try-with-resources here
      getService().executeTransaction(new Realm.Transaction() {
        @Override
        public void execute(Realm realm) {
          realm.delete(Translation.class);
        }
      });
    } finally {
        getService().close();
    }
  }

  @Override
  public void deleteFavouriteTranslations() {
    RealmResults<Translation> translations =
        getService().where(Translation.class)
        .findAll();

    for (Translation translation : translations){
      translation.setIsFavourite(false);
    }
  }

  @Override
  public void addTranslation(final String originalText, final String translatedText, final String language) {
    try { // I could use try-with-resources here
      getService().executeTransaction(new Realm.Transaction() {
        @Override
        public void execute(Realm realm) {
          Translation translation = realm.createObject(Translation.class);
          translation.setOriginalText(originalText);
          translation.setTranslatedText(translatedText);
          translation.setLanguage(language);
          translation.setIsFavourite(false);
        }
      });
    } finally {
      getService().close();
    }
  }

  @Override
  public void toggleTranslationFavourite(String originalName, String language)
      throws EmptyTranslationExceptionBundle {

    Translation translation = getService()
        .where(Translation.class)
        .equalTo("originalName", originalName)
        .equalTo("language", language)
        .findFirst();

    if (translation == null) throw new Resources.NotFoundException("Translation not found");

    translation.setIsFavourite(!translation.getIsFavourite());
  }


  public static class Factory {

    private Factory() {}

    public IRealmTranslateDatabaseService create() {
      return new RealmTranslateDatabaseService();
    }

  }


}
