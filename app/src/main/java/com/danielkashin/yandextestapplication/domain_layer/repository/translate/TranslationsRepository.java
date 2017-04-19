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


public class TranslationsRepository implements ITranslationsRepository {

  private final ITranslationsLocalService localService;

  private final ITranslationsRemoteService remoteService;


  private TranslationsRepository(ITranslationsLocalService localService,
                                 ITranslationsRemoteService remoteService) {
    this.localService = localService;
    this.remoteService = remoteService;
  }

  // ------------------------------ ITranslationRepository methods ----------------------------------


  @Override
  public void deleteTranslations(boolean favorite) {
    localService.deleteTranslations(favorite)
        .executeAsBlocking();
  }

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
  public Pair<Translation, Boolean> getTranslationAndItsSource(final String originalText,
                                                               final String languageText) throws ExceptionBundle {
    try {
      try {
        Long languageId = getLanguageIdByText(languageText);
        DatabaseTranslation databaseTranslation = localService.getTranslation(originalText, languageId.intValue())
            .executeAsBlocking();
        localService.checkDatabaseTranslationForExceptions(databaseTranslation);

        Translation translation = new Translation(databaseTranslation.getOriginalText(),
            databaseTranslation.getTranslatedText(),
            languageText,
            databaseTranslation.isFavorite() == 1);

        return new Pair<>(translation, false);
      } catch (ExceptionBundle exceptionBundle) {

        // get response from the server
        Response<NetworkTranslation> response = remoteService.translate(originalText, languageText)
            .execute();
        remoteService.checkNetworkCodesForExceptions(response.code());

        NetworkTranslation networkTranslation = response.body();

        Translation translation = new Translation(originalText,
            networkTranslation.getText(),
            networkTranslation.getLanguage(),
            false);

        return new Pair<>(translation, true);
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
    DatabaseLanguage databaseLanguage = localService.getLanguage(databaseTranslation.getLanguage())
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

  // ----------------------------------------- private --------------------------------------------

  private Long getLanguageIdByText(String languageText) throws ExceptionBundle {
    DatabaseLanguage language = localService.getLanguage(languageText)
        .executeAsBlocking();

    Long languageId = null;
    if (language != null) {
      // get existing language id
      languageId = language.getId();
    } else {
      // insert new language to the table
      PutResult result = localService.putLanguage(languageText)
          .executeAsBlocking();
      localService.checkInsertResultForExceptions(result);

      // get id of the language we put
      languageId = result.insertedId();
    }

    return languageId;
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
