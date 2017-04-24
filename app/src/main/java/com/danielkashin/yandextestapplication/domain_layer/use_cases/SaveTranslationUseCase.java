package com.danielkashin.yandextestapplication.domain_layer.use_cases;

import android.os.AsyncTask;

import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskVoid;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.danielkashin.yandextestapplication.data_layer.repository.translate.ITranslationsRepository;
import static com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskVoid.RepositoryRunnableVoid;
import static com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskVoid.PostExecuteListenerVoid;

import java.util.concurrent.Executor;


public class SaveTranslationUseCase {

  private final Executor executor;
  private final ITranslationsRepository translateRepository;

  private RepositoryAsyncTaskVoid saveTranslation;


  public SaveTranslationUseCase(Executor executor,
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
      saveTranslation.cancel(false);
      saveTranslation = null;
    }
  }

  public boolean isRunning() {
    return saveTranslation != null
        && saveTranslation.getStatus() == AsyncTask.Status.RUNNING
        && !saveTranslation.isCancelled();
  }

  public void run(final Callbacks uiCallbacks, final Translation translation) {
    RepositoryRunnableVoid setTranslationRunnable = new RepositoryRunnableVoid() {
      @Override
      public void run() throws ExceptionBundle {
        translateRepository.saveTranslation(translation);
      }
    };

    PostExecuteListenerVoid setTranslationListener = new PostExecuteListenerVoid() {
      @Override
      public void onResult() {
        uiCallbacks.onSaveTranslationSuccess();
      }

      @Override
      public void onException(ExceptionBundle exception) {
        uiCallbacks.onSaveTranslationException(exception);
      }
    };

    saveTranslation = new RepositoryAsyncTaskVoid(setTranslationRunnable, setTranslationListener);
    saveTranslation.executeOnExecutor(executor);
  }

  // ------------------------------------ callbacks ----------------------------------------------

  public interface Callbacks {

    void onSaveTranslationSuccess();

    void onSaveTranslationException(ExceptionBundle exceptionBundle);

  }

}
