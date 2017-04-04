package com.danielkashin.yandextestapplication.domain_layer.repository;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Pair;

import com.danielkashin.yandextestapplication.data_layer.entities.remote.NetworkTranslation;
import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.data_layer.services.local.ITranslateLocalService;
import com.danielkashin.yandextestapplication.data_layer.services.remote.ITranslateRemoteService;
import com.danielkashin.yandextestapplication.domain_layer.async_task.remote.NetworkAsyncTask;
import com.danielkashin.yandextestapplication.domain_layer.async_task.remote.VoidAsyncTask;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;


public class TranslateRepository implements ITranslateRepository {

  @NonNull
  private final ITranslateLocalService localService;

  @NonNull
  private final ITranslateRemoteService remoteService;


  private TranslateRepository(@NonNull ITranslateLocalService localService,
                             @NonNull ITranslateRemoteService remoteService) {

    this.localService = localService;
    this.remoteService = remoteService;
  }

  // ------------------------------ ITranslateRepository methods ----------------------------------

  @Override
  public VoidAsyncTask getTranslation(final String originalText, final String language, final GetTranslationCallback callback) {
    NetworkAsyncTask.PostExecuteListener<NetworkTranslation> listener =
        new NetworkAsyncTask.PostExecuteListener<NetworkTranslation>() {
          @Override
          public void onResult(NetworkTranslation result) {
            // create translation
            Translation translation = new Translation(originalText, result.getText(), language, false);

            // save it

            // execute callback
            callback.onResult(translation);
          }

          @Override
          public void onError(ExceptionBundle error) {
            callback.onError(error);
          }
        };

    NetworkAsyncTask<NetworkTranslation> asyncTask =
        new NetworkAsyncTask<>(
            remoteService.translate(originalText, language),
            listener
        );

    return asyncTask;
  }

  @Override
  public VoidAsyncTask getLastTranslation(GetTranslationCallback callback) {
    return null;
  }

  @Override
  public VoidAsyncTask getAllTranslations(int count, int offset, GetTranslationsCallback callback) {
    return null;
  }

  @Override
  public VoidAsyncTask getFavouriteTranslations(int count, int offset, GetTranslationsCallback callback) {
    return null;
  }

  // ------------------------------------ factory -------------------------------------------------

  public static final class Factory {

    private Factory() {}

    public static ITranslateRepository create(ITranslateLocalService localService,
                                              ITranslateRemoteService remoteService){

      return new TranslateRepository(localService, remoteService);
    }
  }
}
