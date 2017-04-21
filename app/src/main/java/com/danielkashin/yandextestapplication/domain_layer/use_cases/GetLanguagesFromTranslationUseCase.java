package com.danielkashin.yandextestapplication.domain_layer.use_cases;

import android.os.AsyncTask;
import android.support.v4.util.Pair;

import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.data_layer.repository.languages.ILanguagesRepository;
import com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskResponse;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Language;
import com.danielkashin.yandextestapplication.domain_layer.pojo.LanguagePair;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.base.IUseCase;

import static com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskResponse.RepositoryRunnableResponse;
import static com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskResponse.PostExecuteListenerResponse;

import java.util.concurrent.Executor;


public class GetLanguagesFromTranslationUseCase implements IUseCase {

  private final Executor executor;
  private final ILanguagesRepository supportedLanguagesRepository;

  private RepositoryAsyncTaskResponse<LanguagePair> getLanguages;


  public GetLanguagesFromTranslationUseCase(Executor executor,
                                            ILanguagesRepository supportedLanguagesRepository) {
    if (executor == null || supportedLanguagesRepository == null) {
      throw new IllegalArgumentException("All arguments of use case must be non null");
    }

    this.executor = executor;
    this.supportedLanguagesRepository = supportedLanguagesRepository;
  }

  // --------------------------------------- IUseCase ---------------------------------------------

  @Override
  public void cancel() {
    if (isRunning()) {
      getLanguages.cancel(false);
      getLanguages = null;
    }
  }

  // ------------------------------------- public methods -----------------------------------------

  public boolean isRunning() {
    return getLanguages != null
        && getLanguages.getStatus() == AsyncTask.Status.RUNNING
        && !getLanguages.isCancelled();
  }


  public void run(final Callbacks callbacks, final Translation translation) {
    PostExecuteListenerResponse<LanguagePair> getLanguagesListener =
        new PostExecuteListenerResponse<LanguagePair>() {
          @Override
          public void onResult(LanguagePair result) {
            callbacks.onGetLanguagesFromTranslationSuccess(new Pair<>(translation, result));
          }

          @Override
          public void onException(ExceptionBundle exception) {
            callbacks.onGetLanguagesFromTranslationException(exception);
          }
        };

    RepositoryRunnableResponse<LanguagePair> getLanguagesRunnable =
        new RepositoryRunnableResponse<LanguagePair>() {
          @Override
          public LanguagePair run() throws ExceptionBundle {
            return supportedLanguagesRepository.getLanguages(
                translation.getLanguageCodes()[0],
                translation.getLanguageCodes()[1]);
          }
        };

    getLanguages = new RepositoryAsyncTaskResponse<>(
        getLanguagesRunnable,
        getLanguagesListener);
    getLanguages.executeOnExecutor(executor);
  }

  // ------------------------------------ inner classes--------------------------------------------

  public interface Callbacks {

    void onGetLanguagesFromTranslationSuccess(Pair<Translation, LanguagePair> result);

    void onGetLanguagesFromTranslationException(ExceptionBundle exception);

  }

}
