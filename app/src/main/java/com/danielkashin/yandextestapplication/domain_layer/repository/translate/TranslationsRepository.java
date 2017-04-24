package com.danielkashin.yandextestapplication.domain_layer.repository.translate;

import android.support.v4.util.Pair;

import com.danielkashin.yandextestapplication.data_layer.entities.translate.local.DatabaseLanguage;
import com.danielkashin.yandextestapplication.data_layer.entities.translate.local.DatabaseTranslation;
import com.danielkashin.yandextestapplication.data_layer.entities.translate.remote.NetworkTranslation;
import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle.Reason;
import com.danielkashin.yandextestapplication.data_layer.services.translate.local.ITranslationsLocalService;
import com.danielkashin.yandextestapplication.data_layer.services.translate.remote.ITranslationsRemoteService;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResult;
import com.pushtorefresh.storio.sqlite.operations.get.PreparedGetListOfObjects;
import com.pushtorefresh.storio.sqlite.operations.put.PutResult;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/*
 * Repository returns and receives data of the needed type, no matter what source it has
 * connects UseCases/Interactors with Services
 * all methods are synchronous, as UseCases is responsible for multithreading
 */
public class TranslationsRepository implements ITranslationsRepository {

  // services are responsible for exception throwing, as well as repository
  private final ITranslationsLocalService localService;
  private final ITranslationsRemoteService remoteService;


  private TranslationsRepository(ITranslationsLocalService localService,
                                 ITranslationsRemoteService remoteService) {
    this.localService = localService;
    this.remoteService = remoteService;
  }

  // --------------------------------- ITranslationRepository -------------------------------------

  //                            -------------- delete ----------------

  @Override
  public void deleteTranslation(Translation translation) throws ExceptionBundle {
    // languages are stored in their own table
    Long languageId = getLanguageIdByText(translation.getLanguageCodePair());

    DatabaseTranslation databaseTranslation = localService.getTranslation(
        translation.getOriginalText(),
        languageId.intValue())
        .executeAsBlocking();

    // services are responsible for exception throwing, as well as repository
    if (databaseTranslation == null) {
      throw new ExceptionBundle(Reason.DELETE_DENIED);
    }

    DeleteResult deleteResult = localService.deleteTranslation(databaseTranslation)
        .executeAsBlocking();
    localService.checkDeleteResultForExceptions(deleteResult);
  }

  @Override
  public void deleteTranslations(boolean favorite) throws ExceptionBundle {
    if (favorite) {
      List<DatabaseTranslation> translations = localService.getAllFavoriteTranslations()
          .executeAsBlocking();

      for (DatabaseTranslation translation : translations) {
        translation.setFavorite(0);
      }

      // unique translations will be overwritten when putting
      localService.putTranslations(translations)
          .executeAsBlocking();
    } else {
      // check that translations contain at least one non-favorite translation
      DatabaseTranslation lastTranslation = localService.getLastTranslationOfType(false)
          .executeAsBlocking();
      if (lastTranslation == null) {
        throw new ExceptionBundle(Reason.DELETE_SOURCE_IS_EMPTY);
      }

      DeleteResult deleteResult = localService.deleteNonFavoriteTranslations()
          .executeAsBlocking();
      // services are responsible for exception throwing, as well as repository
      localService.checkDeleteResultForExceptions(deleteResult);
    }
  }

  //                          ---------------- put -----------------

  @Override
  public void saveTranslation(Translation translation) throws ExceptionBundle {
    // language are stored in their own table, so it is needed to get it`s index before saving translation
    Long languageId = getLanguageIdByText(translation.getLanguageCodePair());

    DatabaseTranslation databaseTranslation = new DatabaseTranslation(
        null,
        translation.getOriginalText(),
        translation.getTranslatedText(),
        languageId,
        translation.ifFavorite() ? 1 : 0
    );

    PutResult result = localService.putTranslation(databaseTranslation)
        .executeAsBlocking();
    localService.checkPutResultForExceptions(result);
  }

  @Override
  public void refreshTranslation(Translation translation) throws ExceptionBundle {
    Long languageId = getLanguageIdByText(translation.getLanguageCodePair());

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

    PutResult result = localService.putTranslation(translationToSave)
        .executeAsBlocking();
    localService.checkPutResultForExceptions(result);
  }

  //                          ---------------- get -----------------

  @Override
  public Translation getRefreshedTranslation(Translation translation) throws ExceptionBundle {
    Long languageId = getLanguageIdByText(translation.getLanguageCodePair());

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

        Long languageId = getLanguageIdByText(languageText);

        DatabaseTranslation databaseTranslation = localService
            .getTranslation(originalText, languageId.intValue())
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


        // return translation from the local service
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


        // return translation from the remote service
        return new Pair<>(translation, Translation.Source.REMOTE);
      }
    } catch (ExceptionBundle exceptionBundle) {
      // throw caught exception bundle next
      throw exceptionBundle;
    } catch (Exception exception) {
      // parse network exception
      remoteService.parseException(exception);
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
    // get list of database translations
    List<DatabaseTranslation> databaseTranslations = localService
        .getTranslations(offset, count, onlyFavourite, searchRequest)
        .executeAsBlocking();
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

      // get id of the language we inserted
      id = result.insertedId();
    }

    return id;
  }

  private DatabaseTranslation getDatabaseTranslationCopy(DatabaseTranslation databaseTranslation) {
    // remove index of translation
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
