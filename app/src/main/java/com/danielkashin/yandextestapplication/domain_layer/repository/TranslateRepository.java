package com.danielkashin.yandextestapplication.domain_layer.repository;

import com.danielkashin.yandextestapplication.data_layer.entities.local.DatabaseLanguage;
import com.danielkashin.yandextestapplication.data_layer.entities.local.DatabaseTranslation;
import com.danielkashin.yandextestapplication.data_layer.entities.remote.NetworkTranslation;
import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle.Reason;
import com.danielkashin.yandextestapplication.data_layer.services.local.ITranslateLocalService;
import com.danielkashin.yandextestapplication.data_layer.services.remote.ITranslateRemoteService;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.pushtorefresh.storio.sqlite.operations.get.PreparedGetListOfObjects;
import com.pushtorefresh.storio.sqlite.operations.put.PutResult;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;


public class TranslateRepository implements ITranslateRepository {

  private final ITranslateLocalService localService;

  private final ITranslateRemoteService remoteService;


  private TranslateRepository(ITranslateLocalService localService,
                              ITranslateRemoteService remoteService) {
    this.localService = localService;
    this.remoteService = remoteService;
  }

  // ------------------------------ ITranslateRepository methods ----------------------------------


  @Override
  public void saveTranslation(Translation translation) throws ExceptionBundle {
    // get current language it or create it
    Long languageId;
    DatabaseLanguage language = localService.getLanguage(translation.getLanguage()).executeAsBlocking();
    if (language != null) {
      // language found -- pick its id
      languageId = language.getId();
    } else {
      // put new language to the table
      PutResult result = localService.putLanguage(translation.getLanguage())
          .executeAsBlocking();
      localService.tryToThrowExceptionBundle(result, true);
      languageId = result.insertedId();
    }

    // create new database translation using picked id and input translation`s information
    DatabaseTranslation databaseTranslation = new DatabaseTranslation(
        null,
        translation.getOriginalText(),
        translation.getTranslatedText(),
        languageId,
        translation.ifFavorite() ? 1 : 0
    );

    // put created translation to database
    PutResult result = localService.putTranslation(databaseTranslation)
        .executeAsBlocking();

    // some results may be not acceptable for us
    localService.tryToThrowExceptionBundle(result, true);
  }

  @Override
  public Translation getTranslation(final String originalText,
                                    final String language) throws ExceptionBundle {
    try {
      // get response from the server
      Response<NetworkTranslation> response = remoteService.translate(originalText, language)
          .execute();

      // some codes shows us that there was exceptional situation
      remoteService.tryToThrowExceptionBundle(response.code());

      return new Translation(originalText, response.body().getText(), response.body().getLanguage(), false);
    } catch (Exception exception) {
      // parse exception
      remoteService.tryToThrowExceptionBundle(exception);
      return null;
    }
  }

  @Override
  public Translation getLastTranslation() throws ExceptionBundle {
    // get translation
    DatabaseTranslation databaseTranslation = localService.getLastTranslation()
        .executeAsBlocking();
    if (databaseTranslation == null) throw new ExceptionBundle(Reason.EMPTY_TRANSLATION);

    // get its language
    DatabaseLanguage databaseLanguage = localService.getLanguage(databaseTranslation.getLanguage())
        .executeAsBlocking();
    if (databaseLanguage == null) throw new ExceptionBundle(Reason.EMPTY_LANGUAGE);

    return new Translation(databaseTranslation.getOriginalText(),
        databaseTranslation.getTranslatedText(),
        databaseLanguage.getLanguage(),
        databaseTranslation.isFavorite() != 0);
  }

  @Override
  public List<Translation> getTranslations(int offset, int count, boolean onlyFavourite) throws ExceptionBundle {
    // get list of database translation
    PreparedGetListOfObjects<DatabaseTranslation> preparedList;
    if (onlyFavourite){
      preparedList = localService.getFavoriteTranslations(offset, count);
    } else {
      preparedList = localService.getTranslations(offset, count);
    }

    List<DatabaseTranslation> databaseTranslations = preparedList.executeAsBlocking();
    if (databaseTranslations.size() == 0) throw new ExceptionBundle(Reason.EMPTY_TRANSLATIONS);

    // parse it into common translations
    List<Translation> translations = new ArrayList<>();
    for (int i = 0; i < databaseTranslations.size(); ++i) {
      DatabaseTranslation databaseTranslation = databaseTranslations.get(i);
      DatabaseLanguage databaseLanguage = localService.getLanguage(databaseTranslation.getLanguage())
          .executeAsBlocking();

      if (databaseLanguage == null) throw new ExceptionBundle(Reason.EMPTY_LANGUAGE);

      translations.add(new Translation(
          databaseTranslation.getOriginalText(),
          databaseTranslation.getTranslatedText(),
          databaseLanguage.getLanguage(),
          databaseTranslation.isFavorite() != 0
      ));
    }

    return translations;
  }

  // ------------------------------------ factory -------------------------------------------------

  public static final class Factory {

    private Factory() {
    }

    public static ITranslateRepository create(ITranslateLocalService localService,
                                              ITranslateRemoteService remoteService) {
      return new TranslateRepository(localService, remoteService);
    }
  }
}
