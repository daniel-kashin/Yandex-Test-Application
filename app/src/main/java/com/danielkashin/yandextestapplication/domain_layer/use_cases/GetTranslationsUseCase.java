package com.danielkashin.yandextestapplication.domain_layer.use_cases;

import android.os.AsyncTask;

import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskResponse;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.danielkashin.yandextestapplication.domain_layer.repository.translate.ITranslationsRepository;

import java.util.ArrayList;
import java.util.concurrent.Executor;

import static com.danielkashin.yandextestapplication.presentation_layer.view.history.HistoryFragment.State.FragmentType;
import static com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskResponse.PostExecuteListenerResponse;
import static com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskResponse.RepositoryRunnableResponse;

public class GetTranslationsUseCase {

  private final Executor executor;
  private final ITranslationsRepository translateRepository;
  private final FragmentType fragmentType;

  private RepositoryAsyncTaskResponse<ArrayList<Translation>> getTranslations;


  public GetTranslationsUseCase(Executor executor,
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
      getTranslations.cancel(false);
      getTranslations = null;
    }
  }

  public boolean isRunning() {
    return getTranslations != null
        && getTranslations.getStatus() == AsyncTask.Status.RUNNING
        && !getTranslations.isCancelled();
  }

  public void run(final Callbacks callbacks, final int offset,
                  final int count, final String searchRequest) {
    if (callbacks == null) {
      throw new IllegalStateException("Callbacks in UseCase must be non null");
    }

    PostExecuteListenerResponse<ArrayList<Translation>> getTranslationsListener =
        new PostExecuteListenerResponse<ArrayList<Translation>>() {
          @Override
          public void onResult(ArrayList<Translation> result) {
            callbacks.onGetTranslationsSuccess(result, offset, searchRequest);
          }

          @Override
          public void onException(ExceptionBundle exception) {
            callbacks.onGetTranslationsException(exception, offset, searchRequest);
          }
        };

    RepositoryRunnableResponse<ArrayList<Translation>> getTranslationsRunnable =
        new RepositoryAsyncTaskResponse.RepositoryRunnableResponse<ArrayList<Translation>>() {
          @Override
          public ArrayList<Translation> run() throws ExceptionBundle {
            return translateRepository.getTranslations(
                offset,
                count,
                fragmentType == FragmentType.ONLY_FAVORITE_HISTORY,
                searchRequest);
          }
        };

    getTranslations = new RepositoryAsyncTaskResponse<>(
        getTranslationsRunnable,
        getTranslationsListener
    );
    getTranslations.executeOnExecutor(executor);
  }

  // ------------------------------------ inner types --------------------------------------------

  public interface Callbacks {

    void onGetTranslationsSuccess(ArrayList<Translation> translations, int offset, String searchRequest);

    void onGetTranslationsException(ExceptionBundle exceptionBundle, int offset, String searchRequest);

  }
}
