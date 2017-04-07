package com.danielkashin.yandextestapplication.data_layer.services.local;


import com.danielkashin.yandextestapplication.data_layer.entities.local.DatabaseLanguage;
import com.danielkashin.yandextestapplication.data_layer.entities.local.DatabaseTranslation;
import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.pushtorefresh.storio.sqlite.operations.get.PreparedGetListOfObjects;
import com.pushtorefresh.storio.sqlite.operations.get.PreparedGetObject;
import com.pushtorefresh.storio.sqlite.operations.put.PreparedPutObject;
import com.pushtorefresh.storio.sqlite.operations.put.PutResult;

import java.util.List;

public interface ITranslateLocalService {

  // -------------------------------- languages ---------------------------------------------------

  PreparedGetObject<DatabaseLanguage> getLanguage(Long index);

  PreparedGetObject<DatabaseLanguage> getLanguage(String language);

  PreparedPutObject<DatabaseLanguage> putLanguage(String language);

  // ------------------------------- translations -------------------------------------------------

  PreparedGetObject<DatabaseTranslation> getLastTranslation();

  PreparedPutObject<DatabaseTranslation> putTranslation(DatabaseTranslation translation);

  PreparedGetListOfObjects<DatabaseTranslation> getTranslations(int offset, int count);

  PreparedGetListOfObjects<DatabaseTranslation> getFavoriteTranslations(int offset, int count);

  // ----------------------------- exception parsing ----------------------------------------------

  void tryToThrowExceptionBundle(PutResult putResult, boolean insertIntended) throws ExceptionBundle;

  void tryToThrowExceptionBundle(DatabaseLanguage databaseLanguage) throws ExceptionBundle;

  void tryToThrowExceptionBundle(DatabaseTranslation databaseTranslation) throws ExceptionBundle;

}
