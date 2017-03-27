package com.danielkashin.yandextestapplication.domain_layer.use_cases;

import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.data_layer.services.yandex_translate.IYandexTranslateNetworkService;
import com.danielkashin.yandextestapplication.domain_layer.async_task.NetworkAsyncTask;
import java.util.concurrent.Executor;
import okhttp3.ResponseBody;


public class TranslateUseCase implements UseCase {

  private final Executor executor;
  private final IYandexTranslateNetworkService networkService;
  private NetworkAsyncTask<ResponseBody> asyncTask;


  public TranslateUseCase(Executor executor, IYandexTranslateNetworkService networkService){
    this.executor = executor;
    this.networkService = networkService;
  }

  @Override
  public void cancel() {
    if (asyncTask != null) asyncTask.cancel(false);
  }

  public void run(final Callbacks callbacks, final String text, final String lang) {
    cancel();

    NetworkAsyncTask.PostExecuteListener<ResponseBody> listener =
        new NetworkAsyncTask.PostExecuteListener<ResponseBody>() {
          @Override
          public void onResult(ResponseBody result) {
            callbacks.onTranslateSuccess(result);
          }

          @Override
          public void onError(ExceptionBundle error) {
            callbacks.onTranslateError(error);
          }
        };

    asyncTask = new NetworkAsyncTask<>(
        networkService.translate(text, lang),
        listener
    );

    asyncTask.executeOnExecutor(executor);
  }


  public interface Callbacks {

    void onTranslateSuccess(ResponseBody result);

    void onTranslateError(Exception exception);

  }
}
