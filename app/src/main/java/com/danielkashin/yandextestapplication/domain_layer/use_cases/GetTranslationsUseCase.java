package com.danielkashin.yandextestapplication.domain_layer.use_cases;

import android.os.AsyncTask;

import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryResponseAsyncTask;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.danielkashin.yandextestapplication.domain_layer.repository.ITranslateRepository;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.base.IUseCase;

import java.util.List;
import java.util.concurrent.Executor;


public class GetTranslationsUseCase implements IUseCase {

  private final Executor executor;
  private final ITranslateRepository translateRepository;
  private final boolean onlyFavorite;
  private RepositoryResponseAsyncTask<List<Translation>> getTranslationsAsyncTask;


  public GetTranslationsUseCase(Executor executor, ITranslateRepository translateRepository,
                                boolean onlyFavorite){
    this.executor = executor;
    this.translateRepository = translateRepository;
    this.onlyFavorite = onlyFavorite;
  }


  @Override
  public void cancel() {
    if (isRunning()){
      getTranslationsAsyncTask.cancel(false);
    }
  }

  public void run(final Callbacks callbacks, final int offset, final int count){
    RepositoryResponseAsyncTask.PostExecuteListener<List<Translation>> listener =
        new RepositoryResponseAsyncTask.PostExecuteListener<List<Translation>>() {
          @Override
          public void onResult(List<Translation> result) {
            callbacks.onGetTranslationsSuccess(result);
          }

          @Override
          public void onException(ExceptionBundle exception) {
            callbacks.onGetTranslationsException(exception);
          }
        };

    RepositoryResponseAsyncTask.RepositoryRunnable<List<Translation>> runnable =
        new RepositoryResponseAsyncTask.RepositoryRunnable<List<Translation>>() {
          @Override
          public List<Translation> run() throws ExceptionBundle {
            return translateRepository.getTranslations(offset, count, onlyFavorite);
          }
        };

    getTranslationsAsyncTask = new RepositoryResponseAsyncTask<List<Translation>>(
        runnable,
        listener
    );
    getTranslationsAsyncTask.executeOnExecutor(executor);
  }

  public boolean isRunning(){
    return getTranslationsAsyncTask != null
        && getTranslationsAsyncTask.getStatus() == AsyncTask.Status.RUNNING;
  }

  public interface Callbacks {

    void onGetTranslationsSuccess(List<Translation> translations);

    void onGetTranslationsException(ExceptionBundle exceptionBundle);

  }
}
