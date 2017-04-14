package com.danielkashin.yandextestapplication.data_layer.services.translation.local;

import com.danielkashin.yandextestapplication.data_layer.database.LanguageContract;
import com.danielkashin.yandextestapplication.data_layer.database.TranslationContract;
import com.danielkashin.yandextestapplication.data_layer.entities.local.DatabaseLanguage;
import com.danielkashin.yandextestapplication.data_layer.entities.local.DatabaseTranslation;
import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.data_layer.services.base.DatabaseService;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.get.PreparedGetListOfObjects;
import com.pushtorefresh.storio.sqlite.operations.get.PreparedGetObject;
import com.pushtorefresh.storio.sqlite.operations.put.PreparedPutObject;
import com.pushtorefresh.storio.sqlite.operations.put.PutResult;
import com.pushtorefresh.storio.sqlite.queries.Query;


public class TranslationLocalService extends DatabaseService implements ITranslationLocalService {

  public TranslationLocalService(StorIOSQLite sqLite) {
    super(sqLite);
  }

  // ------------------------------- ITranslationLocalService ---------------------------------------

  //               -------------------- database languages -------------------

  @Override
  public PreparedGetObject<DatabaseLanguage> getLanguage(String language) {
    return getSQLite().get()
        .object(DatabaseLanguage.class)
        .withQuery(Query.builder()
            .table(LanguageContract.TABLE_NAME)
            .where(LanguageContract.COLUMN_NAME_LANGUAGE + " = \"" + language + "\"")
            .build())
        .prepare();
  }

  @Override
  public PreparedGetObject<DatabaseLanguage> getLanguage(Long index) {
    return getSQLite().get()
        .object(DatabaseLanguage.class)
        .withQuery(Query.builder()
            .table(LanguageContract.TABLE_NAME)
            .where(LanguageContract.COLUMN_NAME_ID + " = \"" + index + "\"")
            .build())
        .prepare();
  }

  @Override
  public PreparedPutObject<DatabaseLanguage> putLanguage(String language) {
    return getSQLite().put()
        .object(new DatabaseLanguage(null, language))
        .prepare();
  }

  //               -------------------- database translations ------------------------

  @Override
  public PreparedGetObject<DatabaseTranslation> getLastTranslation() {
    return getSQLite().get()
        .object(DatabaseTranslation.class)
        .withQuery(Query.builder()
            .table(TranslationContract.TABLE_NAME)
            .orderBy(TranslationContract.COLUMN_NAME_ID + " DESC")
            .limit(1)
            .build())
        .prepare();
  }

  @Override
  public PreparedPutObject<DatabaseTranslation> putTranslation(DatabaseTranslation translation) {
    return getSQLite().put()
        .object(translation)
        .prepare();
  }

  @Override
  public PreparedGetListOfObjects<DatabaseTranslation> getTranslations(int offset,
                                                                       int count,
                                                                       boolean onlyFavourite,
                                                                       String searchRequest) {
    Query.CompleteBuilder queryBuider = Query.builder()
        .table(TranslationContract.TABLE_NAME)
        .orderBy(TranslationContract.COLUMN_NAME_ID + " DESC")
        .limit(offset, count);

    String searchQuery = TranslationContract.getTranslationSearchQuery(onlyFavourite, searchRequest);
    if (searchQuery != null){
      queryBuider = queryBuider.where(searchQuery);
    }

    return getSQLite().get()
        .listOfObjects(DatabaseTranslation.class)
        .withQuery(queryBuider.build())
        .prepare();
  }


  //               ------------------- exceptions parsing --------------------------

  @Override
  public void tryToThrowExceptionBundle(PutResult putResult, boolean insertIntended) throws ExceptionBundle {
    if (putResult.wasNotInserted() && insertIntended) {
      throw new ExceptionBundle(ExceptionBundle.Reason.INSERT_DENIED);
    } else if (putResult.wasNotUpdated()) {
      throw new ExceptionBundle(ExceptionBundle.Reason.UPDATE_DENIED);
    }
  }

  @Override
  public void tryToThrowExceptionBundle(DatabaseLanguage databaseLanguage) throws ExceptionBundle {
    if (databaseLanguage.getId() == null || databaseLanguage.getLanguage() == null) {
      throw new ExceptionBundle(ExceptionBundle.Reason.NULL_FIELD);
    }
  }

  @Override
  public void tryToThrowExceptionBundle(DatabaseTranslation databaseTranslation) throws ExceptionBundle {
    if (databaseTranslation.getId() == null || databaseTranslation.getOriginalText() == null
        || databaseTranslation.getTranslatedText() == null || databaseTranslation.isFavorite() == null) {
      throw new ExceptionBundle(ExceptionBundle.Reason.NULL_FIELD);
    }
  }

  // -------------------------------------- factory -----------------------------------------------

  public static class Factory {

    private Factory() {
    }

    public static ITranslationLocalService create(StorIOSQLite sqLite) {
      return new TranslationLocalService(sqLite);
    }

  }

}
