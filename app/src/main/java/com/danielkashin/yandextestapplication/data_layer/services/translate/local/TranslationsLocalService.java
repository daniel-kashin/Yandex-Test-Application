package com.danielkashin.yandextestapplication.data_layer.services.translate.local;

import com.danielkashin.yandextestapplication.data_layer.contracts.translate.local.LanguageContract;
import com.danielkashin.yandextestapplication.data_layer.contracts.translate.local.TranslationContract;
import com.danielkashin.yandextestapplication.data_layer.entities.translate.local.DatabaseLanguage;
import com.danielkashin.yandextestapplication.data_layer.entities.translate.local.DatabaseTranslation;
import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.data_layer.services.base.DatabaseService;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResult;
import com.pushtorefresh.storio.sqlite.operations.delete.PreparedDeleteByQuery;
import com.pushtorefresh.storio.sqlite.operations.delete.PreparedDeleteObject;
import com.pushtorefresh.storio.sqlite.operations.get.PreparedGetListOfObjects;
import com.pushtorefresh.storio.sqlite.operations.get.PreparedGetObject;
import com.pushtorefresh.storio.sqlite.operations.put.PreparedPutCollectionOfObjects;
import com.pushtorefresh.storio.sqlite.operations.put.PreparedPutObject;
import com.pushtorefresh.storio.sqlite.operations.put.PutResult;
import com.pushtorefresh.storio.sqlite.operations.put.PutResults;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;

import java.util.List;


public class TranslationsLocalService extends DatabaseService implements ITranslationsLocalService {

  private TranslationsLocalService(StorIOSQLite sqLite) {
    super(sqLite);
  }

  // ---------------------------- ITranslationsLocalService ---------------------------------------

  //               ------------------ database languages -------------------

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
            .where(LanguageContract.COLUMN_NAME_ID + " = " + index)
            .build())
        .prepare();
  }

  @Override
  public PreparedPutObject<DatabaseLanguage> putLanguage(String language) {
    return getSQLite().put()
        .object(new DatabaseLanguage(null, language))
        .prepare();
  }

  //               ---------------- database translations --------------------

  //                          ----------- delete -------------

  public PreparedDeleteObject<DatabaseTranslation> deleteTranslation(DatabaseTranslation translation) {
    return getSQLite().delete()
        .object(translation)
        .prepare();
  }

  @Override
  public PreparedDeleteByQuery deleteNotFavoriteTranslations() {
    return getSQLite().delete()
        .byQuery(DeleteQuery.builder()
            .table(TranslationContract.TABLE_NAME)
            .where(TranslationContract.COLUMN_NAME_IS_FAVOURITE + " = " + 0)
            .build()
        ).prepare();
  }

  //                           ------------ get ------------

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
  public PreparedGetObject<DatabaseTranslation> getLastTranslationOfType(boolean favorite) {
    return getSQLite().get()
        .object(DatabaseTranslation.class)
        .withQuery(Query.builder()
            .table(TranslationContract.TABLE_NAME)
            .orderBy(TranslationContract.COLUMN_NAME_ID + " DESC")
            .where(TranslationContract.COLUMN_NAME_IS_FAVOURITE + " = " + (favorite ? 1 : 0))
            .limit(1)
            .build())
        .prepare();
  }

  @Override
  public PreparedGetObject<DatabaseTranslation> getTranslation(String originalText, int languageCode) {
    return getSQLite().get()
        .object(DatabaseTranslation.class)
        .withQuery(Query.builder()
            .table(TranslationContract.TABLE_NAME)
            .where(TranslationContract.getGetTranslationSearchQuery(originalText, languageCode))
            .build())
        .prepare();
  }

  @Override
  public PreparedGetListOfObjects<DatabaseTranslation> getTranslations(int offset,
                                                                       int count,
                                                                       boolean onlyFavourite,
                                                                       String searchRequest) {
    // create builder and add common search bounds
    Query.CompleteBuilder queryBuilder = Query.builder()
        .table(TranslationContract.TABLE_NAME)
        .orderBy(TranslationContract.COLUMN_NAME_ID + " DESC")
        .limit(offset, count);

    // add another bounds if needed
    String searchQuery = TranslationContract.getGetTranslationsSearchQuery(onlyFavourite, searchRequest);
    if (!searchQuery.equals("")) {
      queryBuilder = queryBuilder.where(searchQuery);
    }

    return getSQLite().get()
        .listOfObjects(DatabaseTranslation.class)
        .withQuery(queryBuilder.build())
        .prepare();
  }

  @Override
  public PreparedGetListOfObjects<DatabaseTranslation> getAllFavoriteTranslations() {
    return getSQLite().get()
        .listOfObjects(DatabaseTranslation.class)
        .withQuery(Query.builder()
            .table(TranslationContract.TABLE_NAME)
            .where(TranslationContract.COLUMN_NAME_IS_FAVOURITE + " =" + 1)
            .build())
        .prepare();
  }

  //                          ------------- put ------------

  @Override
  public PreparedPutCollectionOfObjects putTranslations(List<DatabaseTranslation> translations) {
    return getSQLite().put()
        .objects(translations)
        .prepare();
  }

  @Override
  public PreparedPutObject<DatabaseTranslation> putTranslation(DatabaseTranslation translation) {
    return getSQLite().put()
        .object(translation)
        .prepare();
  }

  //               ------------------ exceptions parsing -----------------------


  @Override
  public void checkDeleteResultForExceptions(DeleteResult deleteResult) throws ExceptionBundle {
    if (deleteResult.numberOfRowsDeleted() == 0) {
      throw new ExceptionBundle(ExceptionBundle.Reason.DELETE_DENIED);
    }
  }

  @Override
  public void checkPutResultForExceptions(PutResult putResult) throws ExceptionBundle {
    if (putResult.wasNotInserted() && putResult.wasNotUpdated()) {
      throw new ExceptionBundle(ExceptionBundle.Reason.PUT_DENIED);
    }
  }

  @Override
  public void checkPutResultsForExceptions(PutResults putResults) throws ExceptionBundle {
    if (putResults.numberOfInserts() == 0 && putResults.numberOfUpdates() == 0) {
      throw new ExceptionBundle(ExceptionBundle.Reason.PUT_DENIED);
    }
  }

  @Override
  public void checkInsertResultForExceptions(PutResult putResult) throws ExceptionBundle {
    if (putResult.wasNotInserted() || putResult.insertedId() == null) {
      throw new ExceptionBundle(ExceptionBundle.Reason.PUT_DENIED);
    }
  }

  @Override
  public void checkDatabaseLanguageForExceptions(DatabaseLanguage databaseLanguage) throws ExceptionBundle {
    if (databaseLanguage.getId() == null || databaseLanguage.getLanguage() == null) {
      throw new ExceptionBundle(ExceptionBundle.Reason.NULL_POINTER);
    }
  }

  @Override
  public void checkDatabaseTranslationForExceptions(DatabaseTranslation databaseTranslation) throws ExceptionBundle {
    if (databaseTranslation == null || databaseTranslation.getId() == null
        || databaseTranslation.getOriginalText() == null
        || databaseTranslation.getTranslatedText() == null
        || databaseTranslation.isFavorite() == null) {
      throw new ExceptionBundle(ExceptionBundle.Reason.NULL_POINTER);
    }
  }

  // -------------------------------------- factory -----------------------------------------------

  public static class Factory {

    private Factory() {
    }

    public static ITranslationsLocalService create(StorIOSQLite sqLite) {
      return new TranslationsLocalService(sqLite);
    }

  }

}
