package com.danielkashin.yandextestapplication.domain_layer.use_cases;


import android.os.AsyncTask;

import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskVoid;
import com.danielkashin.yandextestapplication.data_layer.repository.translate.ITranslationsRepository;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.base.IUseCase;
import static com.danielkashin.yandextestapplication.presentation_layer.view.history.HistoryFragment.State.FragmentType;

import java.util.concurrent.Executor;



public class DeleteTranslationsUseCase implements IUseCase {

  private final Executor executor;
  private final ITranslationsRepository translateRepository;
  private final FragmentType fragmentType;

  private RepositoryAsyncTaskVoid deleteTranslations;


  public DeleteTranslationsUseCase(Executor executor,
                                   ITranslationsRepository translateRepository,
                                   FragmentType fragmentType) {
    if (executor == null || translateRepository == null || fragmentType == null) {
      throw new IllegalArgumentException("All arguments of use case must be non null");
    }

    this.executor = executor;
    this.translateRepository = translateRepository;
    this.fragmentType = fragmentType;
  }

  // --------------------------------------- IUseCase ---------------------------------------------

  @Override
  public void cancel() {
    if (isRunning()) {
      deleteTranslations.cancel(false);
      deleteTranslations = null;
    }
  }

  // ------------------------------------- public methods -----------------------------------------

  public boolean isRunning() {
    return deleteTranslations != null
        && deleteTranslations.getStatus() == AsyncTask.Status.RUNNING
        && !deleteTranslations.isCancelled();
  }

  public void run(final Callbacks callbacks) {
    RepositoryAsyncTaskVoid.RepositoryRunnableVoid runnable =
        new RepositoryAsyncTaskVoid.RepositoryRunnableVoid() {
          @Override
          public void run() throws ExceptionBundle {
            translateRepository.deleteTranslations(fragmentType == FragmentType.ONLY_FAVORITE_HISTORY);
          }
        };

    RepositoryAsyncTaskVoid.PostExecuteListenerVoid listener =
        new RepositoryAsyncTaskVoid.PostExecuteListenerVoid() {
          @Override
          public void onResult() {
            if (callbacks != null) {
              callbacks.onDeleteTranslationsSuccess();
            }
          }

          @Override
          public void onException(ExceptionBundle exception) {
            if (callbacks != null) {
              callbacks.onDeleteTranslationsException(exception);
            }
          }
        };


    deleteTranslations = new RepositoryAsyncTaskVoid(
        runnable,
        listener
    );
    deleteTranslations.executeOnExecutor(executor);
  }

  // ------------------------------------ inner classes--------------------------------------------

  public interface Callbacks {

    void onDeleteTranslationsSuccess();

    void onDeleteTranslationsException(ExceptionBundle exception);

  }
}
