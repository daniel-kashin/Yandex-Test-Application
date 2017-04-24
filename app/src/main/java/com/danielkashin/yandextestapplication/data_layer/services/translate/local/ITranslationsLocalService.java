package com.danielkashin.yandextestapplication.data_layer.services.translate.local;

import com.danielkashin.yandextestapplication.data_layer.entities.translate.local.DatabaseLanguage;
import com.danielkashin.yandextestapplication.data_layer.entities.translate.local.DatabaseTranslation;
import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.presentation_layer.presenter.base.Presenter;
import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResult;
import com.pushtorefresh.storio.sqlite.operations.delete.PreparedDeleteByQuery;
import com.pushtorefresh.storio.sqlite.operations.delete.PreparedDeleteObject;
import com.pushtorefresh.storio.sqlite.operations.get.PreparedGetListOfObjects;
import com.pushtorefresh.storio.sqlite.operations.get.PreparedGetObject;
import com.pushtorefresh.storio.sqlite.operations.put.PreparedPutCollectionOfObjects;
import com.pushtorefresh.storio.sqlite.operations.put.PreparedPutObject;
import com.pushtorefresh.storio.sqlite.operations.put.PutResult;
import com.pushtorefresh.storio.sqlite.operations.put.PutResults;

import java.util.ArrayList;
import java.util.List;


public interface ITranslationsLocalService {

  // -------------------------------- languages ---------------------------------------------------

  PreparedGetObject<DatabaseLanguage> getLanguage(Long index);

  PreparedGetObject<DatabaseLanguage> getLanguage(String language);

  PreparedPutObject<DatabaseLanguage> putLanguage(String language);

  // ------------------------------- translations -------------------------------------------------

  //                    -------------- delete ---------------

  PreparedDeleteObject<DatabaseTranslation> deleteTranslation(DatabaseTranslation translation);

  PreparedDeleteByQuery deleteNonFavoriteTranslations();

  //                    ---------------- get ----------------

  PreparedGetObject<DatabaseTranslation> getLastTranslation();

  PreparedGetObject<DatabaseTranslation> getLastTranslationOfType(boolean favorite);

  PreparedGetObject<DatabaseTranslation> getTranslation(String originalText, int languageCode);

  PreparedGetListOfObjects<DatabaseTranslation> getTranslations(int offset,
                                                                int count,
                                                                boolean onlyFavourite,
                                                                String searchRequest);

  PreparedGetListOfObjects<DatabaseTranslation> getAllFavoriteTranslations();

  //                    ---------------- put ----------------

  PreparedPutCollectionOfObjects putTranslations(List<DatabaseTranslation> translations);

  PreparedPutObject<DatabaseTranslation> putTranslation(DatabaseTranslation translation);

  // ----------------------------- exception parsing ----------------------------------------------

  void checkDeleteResultForExceptions(DeleteResult deleteResult) throws ExceptionBundle;

  void checkPutResultsForExceptions(PutResults putResults) throws ExceptionBundle;

  void checkPutResultForExceptions(PutResult putResult) throws ExceptionBundle;

  void checkInsertResultForExceptions(PutResult putResult) throws ExceptionBundle;

  void checkDatabaseLanguageForExceptions(DatabaseLanguage databaseLanguage) throws ExceptionBundle;

  void checkDatabaseTranslationForExceptions(DatabaseTranslation databaseTranslation) throws ExceptionBundle;

}
