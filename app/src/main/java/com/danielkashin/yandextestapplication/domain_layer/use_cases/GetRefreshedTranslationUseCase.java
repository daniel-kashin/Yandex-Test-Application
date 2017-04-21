package com.danielkashin.yandextestapplication.domain_layer.use_cases;


import android.os.AsyncTask;

import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.data_layer.repository.translate.ITranslationsRepository;
import com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskResponse;
import com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskVoid;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.base.IUseCase;

import static com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskResponse.PostExecuteListenerResponse;
import static com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskResponse.RepositoryRunnableResponse;


import java.util.concurrent.Executor;

public class GetRefreshedTranslationUseCase implements IUseCase {

  private final Executor executor;
  private final ITranslationsRepository translateRepository;

  private RepositoryAsyncTaskResponse<Translation> getRefreshedTranslation;


  public GetRefreshedTranslationUseCase(Executor executor,
                                        ITranslationsRepository translateRepository) {
    if (executor == null || translateRepository == null) {
      throw new IllegalArgumentException("All arguments of use case must be non null");
    }

    this.executor = executor;
    this.translateRepository = translateRepository;
  }

  // ---------------------------------------- IUseCase --------------------------------------------

  @Override
  public void cancel() {
    if (isRunning()) {
      getRefreshedTranslation.cancel(false);
      getRefreshedTranslation = null;
    }
  }

  // -------------------------------------- public methods ----------------------------------------

  public boolean isRunning() {
    return getRefreshedTranslation != null
        && getRefreshedTranslation.getStatus() == AsyncTask.Status.RUNNING
        && !getRefreshedTranslation.isCancelled();
  }

  public void run(final Callbacks uiCallbacks, final Translation translation) {
    PostExecuteListenerResponse<Translation> getRefreshedTranslationListener
        = new PostExecuteListenerResponse<Translation>() {
      @Override
      public void onResult(Translation refreshedTranslation) {
        uiCallbacks.onGetRefreshedTranslationResult(refreshedTranslation);
      }

      @Override
      public void onException(ExceptionBundle exception) {
        uiCallbacks.onGetRefreshedTranslationException(exception);
      }
    };

    RepositoryRunnableResponse<Translation> getRefreshedTranslationRunnable =
        new RepositoryRunnableResponse<Translation>() {
          @Override
          public Translation run() throws ExceptionBundle {
            return translateRepository.getRefreshedTranslation(translation);
          }
        };

    getRefreshedTranslation = new RepositoryAsyncTaskResponse<>(
        getRefreshedTranslationRunnable,
        getRefreshedTranslationListener);
    getRefreshedTranslation.executeOnExecutor(executor);
  }

  // ------------------------------------ callbacks ----------------------------------------------

  public interface Callbacks {

    void onGetRefreshedTranslationResult(Translation translation);

    void onGetRefreshedTranslationException(ExceptionBundle exceptionBundle);

  }


}
