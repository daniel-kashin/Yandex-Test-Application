package com.danielkashin.yandextestapplication.domain_layer.use_cases.remote;

import android.os.AsyncTask;
import android.util.Pair;

import com.danielkashin.yandextestapplication.data_layer.entities.remote.NetworkTranslation;
import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.data_layer.services.remote.ITranslateNetworkService;
import com.danielkashin.yandextestapplication.domain_layer.async_task.remote.NetworkAsyncTask;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.base.UseCase;

import java.util.concurrent.Executor;


public class TranslateUseCase implements UseCase {

  private final Executor executor;
  private final ITranslateNetworkService networkService;
  private NetworkAsyncTask<NetworkTranslation> asyncTask;


  public TranslateUseCase(Executor executor, ITranslateNetworkService networkService) {
    this.executor = executor;
    this.networkService = networkService;
  }

  @Override
  public void cancel() {
    if (asyncTask != null && asyncTask.getStatus() == AsyncTask.Status.RUNNING){
      asyncTask.cancel(false);
    }
  }

  public void run(final Callbacks callbacks, final String text, final String lang) {
    NetworkAsyncTask.PostExecuteListener<NetworkTranslation> listener =
        new NetworkAsyncTask.PostExecuteListener<NetworkTranslation>() {
          @Override
          public void onResult(NetworkTranslation result) {
            callbacks.onTranslateSuccess(new Pair<>(text, result));
          }

          @Override
          public void onError(ExceptionBundle error) {
            callbacks.onTranslateError(error);
          }
        };

    asyncTask = new NetworkAsyncTask<NetworkTranslation>(
        networkService.translate(text, lang),
        listener
    );

    asyncTask.executeOnExecutor(executor);
  }

  public boolean isRunning() {
    return asyncTask != null && asyncTask.getStatus() == AsyncTask.Status.RUNNING;
  }


  public interface Callbacks {

    void onTranslateSuccess(Pair<String, NetworkTranslation> result);

    void onTranslateError(ExceptionBundle exception);

  }
}
