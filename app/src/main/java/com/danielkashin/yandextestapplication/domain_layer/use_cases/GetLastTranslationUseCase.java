package com.danielkashin.yandextestapplication.domain_layer.use_cases;

import android.os.AsyncTask;

import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryResponseAsyncTask;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.danielkashin.yandextestapplication.domain_layer.repository.ITranslateRepository;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.base.IUseCase;

import java.util.concurrent.Executor;


public class GetLastTranslationUseCase implements IUseCase {

  private final Executor executor;
  private final ITranslateRepository repository;
  private RepositoryResponseAsyncTask<Translation> getLastTranslationAsyncTask;


  public GetLastTranslationUseCase(Executor executor, ITranslateRepository repository) {
    this.executor = executor;
    this.repository = repository;
  }


  @Override
  public void cancel() {
    if (isRunning()) {
      getLastTranslationAsyncTask.cancel(false);
    }
  }


  public void run(final Callbacks callbacks) {
    RepositoryResponseAsyncTask.PostExecuteListener<Translation> listener =
        new RepositoryResponseAsyncTask.PostExecuteListener<Translation>(){
          @Override
          public void onResult(Translation result) {
            callbacks.onGetLastTranslationSuccess(result);
          }

          @Override
          public void onException(ExceptionBundle exception) {
            callbacks.onGetLastTranslationException(exception);
          }
        };

    RepositoryResponseAsyncTask.RepositoryRunnable<Translation> repositoryRunnable =
        new RepositoryResponseAsyncTask.RepositoryRunnable<Translation>() {
          @Override
          public Translation run() throws ExceptionBundle {
            return repository.getLastTranslation();
          }
        };

    getLastTranslationAsyncTask = new RepositoryResponseAsyncTask<>(
        repositoryRunnable,
        listener);
    getLastTranslationAsyncTask.executeOnExecutor(executor);
  }

  public boolean isRunning() {
    return getLastTranslationAsyncTask != null
        && getLastTranslationAsyncTask.getStatus() == AsyncTask.Status.RUNNING;
  }

  public interface Callbacks {

    void onGetLastTranslationSuccess(Translation translation);

    void onGetLastTranslationException(ExceptionBundle exception);

  }
}
