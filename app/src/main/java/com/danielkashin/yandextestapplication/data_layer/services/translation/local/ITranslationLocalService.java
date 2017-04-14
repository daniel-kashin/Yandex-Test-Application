package com.danielkashin.yandextestapplication.data_layer.services.translation.local;


import com.danielkashin.yandextestapplication.data_layer.entities.local.DatabaseLanguage;
import com.danielkashin.yandextestapplication.data_layer.entities.local.DatabaseTranslation;
import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.pushtorefresh.storio.sqlite.operations.get.PreparedGetListOfObjects;
import com.pushtorefresh.storio.sqlite.operations.get.PreparedGetObject;
import com.pushtorefresh.storio.sqlite.operations.put.PreparedPutObject;
import com.pushtorefresh.storio.sqlite.operations.put.PutResult;

public interface ITranslationLocalService {

  // -------------------------------- languages ---------------------------------------------------

  PreparedGetObject<DatabaseLanguage> getLanguage(Long index);

  PreparedGetObject<DatabaseLanguage> getLanguage(String language);

  PreparedPutObject<DatabaseLanguage> putLanguage(String language);

  // ------------------------------- translations -------------------------------------------------

  PreparedGetObject<DatabaseTranslation> getLastTranslation();

  PreparedPutObject<DatabaseTranslation> putTranslation(DatabaseTranslation translation);

  PreparedGetListOfObjects<DatabaseTranslation> getTranslations(int offset,
                                                                int count,
                                                                boolean onlyFavourite,
                                                                String searchRequest);

  // ----------------------------- exception parsing ----------------------------------------------

  void tryToThrowExceptionBundle(PutResult putResult, boolean insertIntended) throws ExceptionBundle;

  void tryToThrowExceptionBundle(DatabaseLanguage databaseLanguage) throws ExceptionBundle;

  void tryToThrowExceptionBundle(DatabaseTranslation databaseTranslation) throws ExceptionBundle;

}
