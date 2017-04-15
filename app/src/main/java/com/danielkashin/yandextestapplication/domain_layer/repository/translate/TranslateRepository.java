package com.danielkashin.yandextestapplication.domain_layer.repository.translate;

import com.danielkashin.yandextestapplication.data_layer.entities.translate.local.DatabaseLanguage;
import com.danielkashin.yandextestapplication.data_layer.entities.translate.local.DatabaseTranslation;
import com.danielkashin.yandextestapplication.data_layer.entities.translate.remote.NetworkTranslation;
import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle.Reason;
import com.danielkashin.yandextestapplication.data_layer.services.translate.local.ITranslateLocalService;
import com.danielkashin.yandextestapplication.data_layer.services.translate.remote.ITranslateRemoteService;
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

  // ------------------------------ ITranslationRepository methods ----------------------------------

  @Override
  public void saveTranslation(Translation translation) throws ExceptionBundle {
    // get current language id or create it
    Long languageId;
    DatabaseLanguage language = localService.getLanguage(translation.getLanguageCodePair()).executeAsBlocking();
    if (language != null) {
      // language found -- pick its id
      languageId = language.getId();
    } else {
      // put new language to the table
      PutResult result = localService.putLanguage(translation.getLanguageCodePair())
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

  @Override
  public String getLanguage(String languageCode) {
    return null;
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
