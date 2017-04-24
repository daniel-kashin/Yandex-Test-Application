package com.danielkashin.yandextestapplication.domain_layer.use_cases;

import android.os.AsyncTask;

import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskResponse;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.danielkashin.yandextestapplication.data_layer.repository.translate.ITranslationsRepository;

import java.util.ArrayList;
import java.util.concurrent.Executor;

import static com.danielkashin.yandextestapplication.presentation_layer.view.history.HistoryFragment.State.FragmentType;


public class GetTranslationsUseCase {

  private final Executor executor;
  private final ITranslationsRepository translateRepository;
  private final FragmentType fragmentType;

  private RepositoryAsyncTaskResponse<ArrayList<Translation>> getTranslationsAsyncTask;


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
      getTranslationsAsyncTask.cancel(false);
      getTranslationsAsyncTask = null;
    }
  }

  public boolean isRunning() {
    return getTranslationsAsyncTask != null
        && getTranslationsAsyncTask.getStatus() == AsyncTask.Status.RUNNING
        && !getTranslationsAsyncTask.isCancelled();
  }

  public void run(final Callbacks callbacks, final int offset,
                  final int count, final String searchRequest) {
    RepositoryAsyncTaskResponse.PostExecuteListenerResponse<ArrayList<Translation>> listener =
        new RepositoryAsyncTaskResponse.PostExecuteListenerResponse<ArrayList<Translation>>() {
          @Override
          public void onResult(ArrayList<Translation> result) {
            if (callbacks != null) {
              callbacks.onGetTranslationsSuccess(result, offset, searchRequest);
            }
          }

          @Override
          public void onException(ExceptionBundle exception) {
            if (callbacks != null) {
              callbacks.onGetTranslationsException(exception, offset, searchRequest);
            }
          }
        };

    RepositoryAsyncTaskResponse.RepositoryRunnableResponse<ArrayList<Translation>> runnable =
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

    getTranslationsAsyncTask = new RepositoryAsyncTaskResponse<>(
        runnable,
        listener
    );
    getTranslationsAsyncTask.executeOnExecutor(executor);
  }

  // ------------------------------------ inner classes--------------------------------------------

  public interface Callbacks {

    void onGetTranslationsSuccess(ArrayList<Translation> translations, int offset, String searchRequest);

    void onGetTranslationsException(ExceptionBundle exceptionBundle, int offset, String searchRequest);

  }
}
