package com.danielkashin.yandextestapplication.domain_layer.use_cases;

import android.os.AsyncTask;

import com.danielkashin.yandextestapplication.data_layer.entities.remote.Translation;
import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.data_layer.services.remote.IYandexTranslateNetworkService;
import com.danielkashin.yandextestapplication.domain_layer.async_task.YandexNetworkAsyncTask;

import java.util.concurrent.Executor;


public class YandexTranslateUseCase implements UseCase {

  private final Executor executor;
  private final IYandexTranslateNetworkService networkService;
  private YandexNetworkAsyncTask<Translation> asyncTask;


  public YandexTranslateUseCase(Executor executor, IYandexTranslateNetworkService networkService) {
    this.executor = executor;
    this.networkService = networkService;
  }

  @Override
  public void cancel() {
    if (asyncTask != null) asyncTask.cancel(false);
  }

  public void run(final Callbacks callbacks, final String text, final String lang) {
    cancel();

    YandexNetworkAsyncTask.PostExecuteListener<Translation> listener =
        new YandexNetworkAsyncTask.PostExecuteListener<Translation>() {
          @Override
          public void onResult(Translation result) {
            callbacks.onTranslateSuccess(result);
          }

          @Override
          public void onError(ExceptionBundle error) {
            callbacks.onTranslateError(error);
          }
        };

    asyncTask = new YandexNetworkAsyncTask<Translation>(
        networkService.translate(text, lang),
        listener
    );

    asyncTask.executeOnExecutor(executor);
  }

  public boolean isRunning() {
    return asyncTask != null && asyncTask.getStatus() == AsyncTask.Status.RUNNING;
  }


  public interface Callbacks {

    void onTranslateSuccess(Translation result);

    void onTranslateError(ExceptionBundle exception);

  }
}
