package com.danielkashin.yandextestapplication.domain_layer.use_cases;

import android.os.AsyncTask;

import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.data_layer.repository.translate.ITranslationsRepository;
import com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskVoid;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;

import static com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskVoid.RepositoryRunnableVoid;
import static com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskVoid.PostExecuteListenerVoid;

import java.util.concurrent.Executor;


public class DeleteTranslationUseCase {

  private final Executor executor;
  private final ITranslationsRepository translateRepository;

  private RepositoryAsyncTaskVoid deleteTranslation;


  public DeleteTranslationUseCase(Executor executor,
                                  ITranslationsRepository translateRepository) {
    if (executor == null || translateRepository == null) {
      throw new IllegalArgumentException("All arguments of use case must be non null");
    }

    this.executor = executor;
    this.translateRepository = translateRepository;
  }

  // ------------------------------------- public methods -----------------------------------------

  public void cancel() {
    if (isRunning()) {
      deleteTranslation.cancel(false);
      deleteTranslation = null;
    }
  }

  public boolean isRunning() {
    return deleteTranslation != null
        && deleteTranslation.getStatus() == AsyncTask.Status.RUNNING
        && !deleteTranslation.isCancelled();
  }

  public void run(final Callbacks callbacks, final Translation translation) {
    PostExecuteListenerVoid deleteListener = new PostExecuteListenerVoid() {
      @Override
      public void onResult() {
        callbacks.onDeleteTranslationSuccess(translation);
      }

      @Override
      public void onException(ExceptionBundle exception) {
        callbacks.onDeleteTranslationException(exception);
      }
    };

    RepositoryRunnableVoid deleteRunnable = new RepositoryRunnableVoid() {
      @Override
      public void run() throws ExceptionBundle {
        translateRepository.deleteTranslation(translation);
      }
    };

    deleteTranslation = new RepositoryAsyncTaskVoid(deleteRunnable, deleteListener);
    deleteTranslation.executeOnExecutor(executor);
  }

  // ------------------------------------ inner classes--------------------------------------------

  public interface Callbacks {

    void onDeleteTranslationSuccess(Translation translation);

    void onDeleteTranslationException(ExceptionBundle exception);

  }

}
