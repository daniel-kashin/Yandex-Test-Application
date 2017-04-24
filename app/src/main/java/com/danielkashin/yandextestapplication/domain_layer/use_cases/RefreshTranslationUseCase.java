package com.danielkashin.yandextestapplication.domain_layer.use_cases;

import android.os.AsyncTask;

import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskVoid;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.danielkashin.yandextestapplication.domain_layer.repository.translate.ITranslationsRepository;

import static com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskVoid.RepositoryRunnableVoid;
import static com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskVoid.PostExecuteListenerVoid;

import java.util.concurrent.Executor;


public class RefreshTranslationUseCase {

  private final Executor executor;
  private final ITranslationsRepository translateRepository;

  private RepositoryAsyncTaskVoid refreshTranslation;


  public RefreshTranslationUseCase(Executor executor,
                                   ITranslationsRepository translateRepository) {
    if (executor == null || translateRepository == null) {
      throw new IllegalArgumentException("All arguments of use case must be non null");
    }

    this.executor = executor;
    this.translateRepository = translateRepository;
  }

  // -------------------------------------- public methods ----------------------------------------

  public void cancel() {
    if (isRunning()) {
      refreshTranslation.cancel(false);
      refreshTranslation = null;
    }
  }

  public boolean isRunning() {
    return refreshTranslation != null
        && refreshTranslation.getStatus() == AsyncTask.Status.RUNNING
        && !refreshTranslation.isCancelled();
  }

  public void run(final Callbacks callbacks, final Translation translation) {
    if (callbacks == null) {
      throw new IllegalStateException("Callbacks in UseCase must be non null");
    }

    PostExecuteListenerVoid refreshTranslationListener = new PostExecuteListenerVoid() {
      @Override
      public void onResult() {
        callbacks.onRefreshTranslationSuccess();
      }

      @Override
      public void onException(ExceptionBundle exception) {
        callbacks.onRefreshTranslationException(exception);
      }
    };

    RepositoryRunnableVoid refreshTranslationRunnable = new RepositoryRunnableVoid() {
      @Override
      public void run() throws ExceptionBundle {
        translateRepository.refreshTranslation(translation);
      }
    };

    refreshTranslation = new RepositoryAsyncTaskVoid(
        refreshTranslationRunnable,
        refreshTranslationListener);
    refreshTranslation.executeOnExecutor(executor);
  }

  // ------------------------------------ inner types --------------------------------------------

  public interface Callbacks {

    void onRefreshTranslationSuccess();

    void onRefreshTranslationException(ExceptionBundle exceptionBundle);

  }

}
