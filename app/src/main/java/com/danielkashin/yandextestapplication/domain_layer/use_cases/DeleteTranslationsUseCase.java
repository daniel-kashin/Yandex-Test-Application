package com.danielkashin.yandextestapplication.domain_layer.use_cases;

import android.os.AsyncTask;

import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskVoid;
import com.danielkashin.yandextestapplication.domain_layer.repository.translate.ITranslationsRepository;

import static com.danielkashin.yandextestapplication.presentation_layer.view.history.HistoryFragment.State.FragmentType;

import java.util.concurrent.Executor;

/*
* connects presentation layer and repository. presenters implement callback
* methods and store UseCases as private final variables
*/
public class DeleteTranslationsUseCase {

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

  // ------------------------------------- public methods -----------------------------------------

  public void cancel() {
    if (isRunning()) {
      deleteTranslations.cancel(false);
      deleteTranslations = null;
    }
  }

  public boolean isRunning() {
    return deleteTranslations != null
        && deleteTranslations.getStatus() == AsyncTask.Status.RUNNING
        && !deleteTranslations.isCancelled();
  }

  public void run(final Callbacks callbacks) {
    if (callbacks == null) {
      throw new IllegalStateException("Callbacks in UseCase must be non null");
    }

    RepositoryAsyncTaskVoid.RepositoryRunnableVoid deleteRunnable =
        new RepositoryAsyncTaskVoid.RepositoryRunnableVoid() {
          @Override
          public void run() throws ExceptionBundle {
            translateRepository.deleteTranslations(fragmentType == FragmentType.ONLY_FAVORITE_HISTORY);
          }
        };

    RepositoryAsyncTaskVoid.PostExecuteListenerVoid deleteListener =
        new RepositoryAsyncTaskVoid.PostExecuteListenerVoid() {
          @Override
          public void onResult() {
            callbacks.onDeleteTranslationsSuccess();
          }

          @Override
          public void onException(ExceptionBundle exception) {
            callbacks.onDeleteTranslationsException(exception);
          }
        };

    deleteTranslations = new RepositoryAsyncTaskVoid(
        deleteRunnable,
        deleteListener
    );
    deleteTranslations.executeOnExecutor(executor);
  }

  // ------------------------------------ inner types --------------------------------------------

  public interface Callbacks {

    void onDeleteTranslationsSuccess();

    void onDeleteTranslationsException(ExceptionBundle exception);

  }
}
