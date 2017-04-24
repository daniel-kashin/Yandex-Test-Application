package com.danielkashin.yandextestapplication.domain_layer.use_cases;

import android.os.AsyncTask;

import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.domain_layer.repository.translate.ITranslationsRepository;
import com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskResponse;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;

import static com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskResponse.PostExecuteListenerResponse;
import static com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskResponse.RepositoryRunnableResponse;


import java.util.concurrent.Executor;


public class GetRefreshedTranslationUseCase {

  private final Executor executor;
  private final ITranslationsRepository translateRepository;

  private RepositoryAsyncTaskResponse<Translation> getTranslation;


  public GetRefreshedTranslationUseCase(Executor executor,
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
      getTranslation.cancel(false);
      getTranslation = null;
    }
  }

  public boolean isRunning() {
    return getTranslation != null
        && getTranslation.getStatus() == AsyncTask.Status.RUNNING
        && !getTranslation.isCancelled();
  }

  public void run(final Callbacks callbacks, final Translation translation) {
    if (callbacks == null) {
      throw new IllegalStateException("Callbacks in UseCase must be non null");
    }

    PostExecuteListenerResponse<Translation> getTranslationListener
        = new PostExecuteListenerResponse<Translation>() {
      @Override
      public void onResult(Translation refreshedTranslation) {
        callbacks.onGetRefreshedTranslationResult(refreshedTranslation);
      }

      @Override
      public void onException(ExceptionBundle exception) {
        callbacks.onGetRefreshedTranslationException(exception);
      }
    };

    RepositoryRunnableResponse<Translation> getTranslationRunnable =
        new RepositoryRunnableResponse<Translation>() {
          @Override
          public Translation run() throws ExceptionBundle {
            return translateRepository.getRefreshedTranslation(translation);
          }
        };

    getTranslation = new RepositoryAsyncTaskResponse<>(
        getTranslationRunnable,
        getTranslationListener);
    getTranslation.executeOnExecutor(executor);
  }

  // ------------------------------------ inner types --------------------------------------------

  public interface Callbacks {

    void onGetRefreshedTranslationResult(Translation translation);

    void onGetRefreshedTranslationException(ExceptionBundle exceptionBundle);

  }
}
