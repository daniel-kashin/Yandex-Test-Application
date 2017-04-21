package com.danielkashin.yandextestapplication.data_layer.repository.translate;


import android.support.v4.util.Pair;

import com.danielkashin.yandextestapplication.data_layer.entities.translate.local.DatabaseLanguage;
import com.danielkashin.yandextestapplication.data_layer.entities.translate.local.DatabaseTranslation;
import com.danielkashin.yandextestapplication.data_layer.entities.translate.remote.NetworkTranslation;
import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle.Reason;
import com.danielkashin.yandextestapplication.data_layer.services.translate.local.ITranslationsLocalService;
import com.danielkashin.yandextestapplication.data_layer.services.translate.remote.ITranslationsRemoteService;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.pushtorefresh.storio.sqlite.operations.get.PreparedGetListOfObjects;
import com.pushtorefresh.storio.sqlite.operations.put.PutResult;
import com.pushtorefresh.storio.sqlite.operations.put.PutResults;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;


public class TranslationsRepository implements ITranslationsRepository {

  private final ITranslationsLocalService localService;

  private final ITranslationsRemoteService remoteService;


  private TranslationsRepository(ITranslationsLocalService localService,
                                 ITranslationsRemoteService remoteService) {
    this.localService = localService;
    this.remoteService = remoteService;
  }

  // ------------------------------ ITranslationRepository methods ----------------------------------

  //                          -------------- delete ----------------

  @Override
  public void deleteTranslations(boolean favorite) throws ExceptionBundle {
    if (favorite) {
      List<DatabaseTranslation> translations = localService.getAllFavoriteTranslations()
          .executeAsBlocking();
      if (translations.size() != 0) {
        for (DatabaseTranslation translation : translations) {
          translation.setFavorite(0);
        }

        PutResults results = localService.putTranslations(translations)
            .executeAsBlocking();
        localService.checkPutResultsForException(results);
      }
    } else {
      localService.deleteNotFavoriteTranslations()
          .executeAsBlocking();
    }
  }

  //                          ---------------- put -----------------

  @Override
  public void saveTranslation(Translation translation) throws ExceptionBundle {
    // we store language in another table, so must get it before saving translation
    Long languageId = getLanguageIdByText(translation.getLanguageCodePair());

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
    localService.checkPutResultForExceptions(result);
  }

  @Override
  public void refreshTranslation(Translation translation)
      throws ExceptionBundle {
    Long languageId = getLanguageIdByText(translation.getLanguageCodePair());

    // get translation and throw exception if it is not valid
    DatabaseTranslation databaseTranslation = localService.getTranslation(
        translation.getOriginalText(),
        languageId.intValue())
        .executeAsBlocking();
    localService.checkDatabaseTranslationForExceptions(databaseTranslation);

    DatabaseTranslation translationToSave = new DatabaseTranslation(databaseTranslation.getId(),
        databaseTranslation.getOriginalText(),
        databaseTranslation.getTranslatedText(),
        databaseTranslation.getLanguageId(),
        translation.ifFavorite() ? 1 : 0);

    // put created translation to database
    PutResult result = localService.putTranslation(translationToSave)
        .executeAsBlocking();
    localService.checkPutResultForExceptions(result);
  }

  //                          ---------------- get -----------------


  @Override
  public Translation getRefreshedTranslation(Translation translation) throws ExceptionBundle {
    Long languageId = getLanguageIdByText(translation.getLanguageCodePair());

    // get translation and throw exception if it is not valid
    DatabaseTranslation databaseTranslation = localService.getTranslation(
        translation.getOriginalText(),
        languageId.intValue())
        .executeAsBlocking();
    localService.checkDatabaseTranslationForExceptions(databaseTranslation);

    return new Translation(databaseTranslation.getOriginalText(),
        databaseTranslation.getTranslatedText(),
        translation.getLanguageCodePair(),
        databaseTranslation.isFavorite() == 1);
  }

  @Override
  public Pair<Translation, Translation.Source> getTranslationAndItsSource(final String originalText,
                                                                          final String languageText) throws ExceptionBundle {
    try {
      try {
        // get cached translation from the local service if possible

        // get id of the language as languages are stored in another table
        Long languageId = getLanguageIdByText(languageText);

        // get translation and throw exception if it is not valid
        DatabaseTranslation databaseTranslation = localService.getTranslation(originalText, languageId.intValue())
            .executeAsBlocking();
        localService.checkDatabaseTranslationForExceptions(databaseTranslation);

        // put translation to the top
        DatabaseTranslation translationToPut = getDatabaseTranslationCopy(databaseTranslation);
        PutResult putResult = localService.putTranslation(translationToPut)
            .executeAsBlocking();
        localService.checkPutResultForExceptions(putResult);

        Translation translation = new Translation(databaseTranslation.getOriginalText(),
            databaseTranslation.getTranslatedText(),
            languageText,
            databaseTranslation.isFavorite() == 1);


        return new Pair<>(translation, Translation.Source.LOCAL);
      } catch (ExceptionBundle exceptionBundle) {
        // database does not contain translation -> get it from the remote service

        Response<NetworkTranslation> response = remoteService.translate(originalText, languageText)
            .execute();
        remoteService.checkNetworkCodesForExceptions(response.code());

        NetworkTranslation networkTranslation = response.body();

        Translation translation = new Translation(originalText,
            networkTranslation.getText(),
            networkTranslation.getLanguage(),
            false);


        return new Pair<>(translation, Translation.Source.REMOTE);
      }
    } catch (ExceptionBundle exceptionBundle) {
      // throw caught exception bundle next
      throw exceptionBundle;
    } catch (Exception exception) {
      // parse network exception
      remoteService.checkNetworkCodesForExceptions(exception);
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
    DatabaseLanguage databaseLanguage = localService.getLanguage(databaseTranslation.getLanguageId())
        .executeAsBlocking();
    if (databaseLanguage == null) throw new ExceptionBundle(Reason.EMPTY_LANGUAGE);

    return new Translation(databaseTranslation.getOriginalText(),
        databaseTranslation.getTranslatedText(),
        databaseLanguage.getLanguage(),
        databaseTranslation.isFavorite() != 0);
  }

  @Override
  public ArrayList<Translation> getTranslations(int offset, int count, boolean onlyFavourite,
                                                String searchRequest) throws ExceptionBundle {
    // get list of database translation
    PreparedGetListOfObjects<DatabaseTranslation> preparedList;
    preparedList = localService.getTranslations(offset, count, onlyFavourite, searchRequest);
    List<DatabaseTranslation> databaseTranslations = preparedList.executeAsBlocking();
    if (databaseTranslations.size() == 0) throw new ExceptionBundle(Reason.EMPTY_TRANSLATIONS);

    // parse it into common translations
    ArrayList<Translation> translations = new ArrayList<>();
    for (int i = 0; i < databaseTranslations.size(); ++i) {
      DatabaseTranslation databaseTranslation = databaseTranslations.get(i);
      DatabaseLanguage databaseLanguage = localService.getLanguage(databaseTranslation.getLanguageId())
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

  // ----------------------------------------- private --------------------------------------------

  private Long getLanguageIdByText(String languageText) throws ExceptionBundle {
    DatabaseLanguage language = localService.getLanguage(languageText)
        .executeAsBlocking();

    Long id = null;
    if (language != null) {
      // get existing language id
      id = language.getId();
    } else {
      // insert new language to the table
      PutResult result = localService.putLanguage(languageText)
          .executeAsBlocking();
      localService.checkInsertResultForExceptions(result);

      // get id of the language we put
      id = result.insertedId();
    }

    return id;
  }

  private DatabaseTranslation getDatabaseTranslationCopy(DatabaseTranslation databaseTranslation) {
    return new DatabaseTranslation(null,
        databaseTranslation.getOriginalText(),
        databaseTranslation.getTranslatedText(),
        databaseTranslation.getLanguageId(),
        databaseTranslation.isFavorite());
  }

  // --------------------------------------- inner types ------------------------------------------

  public static final class Factory {

    private Factory() {
    }

    public static ITranslationsRepository create(ITranslationsLocalService localService,
                                                 ITranslationsRemoteService remoteService) {
      return new TranslationsRepository(localService, remoteService);
    }
  }
}
