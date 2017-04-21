package com.danielkashin.yandextestapplication.domain_layer.use_cases;

import android.os.AsyncTask;

import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.data_layer.repository.translate.ITranslationsRepository;
import com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskVoid;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.base.IUseCase;

import static com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskVoid.RepositoryRunnableVoid;
import static com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskVoid.PostExecuteListenerVoid;

import java.util.concurrent.Executor;


public class DeleteTranslationUseCase implements IUseCase {

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

  // --------------------------------------- IUseCase ---------------------------------------------

  @Override
  public void cancel() {
    if (isRunning()) {
      deleteTranslation.cancel(false);
      deleteTranslation = null;
    }
  }

  // ------------------------------------- public methods -----------------------------------------

  public boolean isRunning() {
    return deleteTranslation != null
        && deleteTranslation.getStatus() == AsyncTask.Status.RUNNING
        && !deleteTranslation.isCancelled();
  }

  public void run(final DeleteTranslationsUseCase.Callbacks callbacks, final Translation translation) {
    PostExecuteListenerVoid deleteListener = new PostExecuteListenerVoid() {
      @Override
      public void onResult() {
        callbacks.onDeleteTranslationsSuccess();
      }

      @Override
      public void onException(ExceptionBundle exception) {
        callbacks.onDeleteTranslationsException(exception);
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

    void onDeleteTranslationSuccess();

    void onDeleteTranslationException(ExceptionBundle exception);

  }

}
