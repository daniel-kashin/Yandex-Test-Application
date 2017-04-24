package com.danielkashin.yandextestapplication.domain_layer.use_cases;

import android.os.AsyncTask;
import android.support.v4.util.Pair;
import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskResponse;
import com.danielkashin.yandextestapplication.domain_layer.pojo.LanguagePair;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.danielkashin.yandextestapplication.domain_layer.repository.languages.ISupportedLanguagesRepository;
import com.danielkashin.yandextestapplication.domain_layer.repository.translate.ITranslationsRepository;
import java.util.concurrent.Executor;


public class GetLastTranslationUseCase {

  private final Executor executor;
  private final ITranslationsRepository translateRepository;
  private final ISupportedLanguagesRepository supportedLanguagesRepository;

  private RepositoryAsyncTaskResponse<Translation> getTranslation;


  public GetLastTranslationUseCase(Executor executor,
                                   ITranslationsRepository translateRepository,
                                   ISupportedLanguagesRepository supportedLanguagesRepository) {
    if (executor == null || translateRepository == null || supportedLanguagesRepository == null){
      throw new IllegalArgumentException("All arguments of use case must be non null");
    }

    this.executor = executor;
    this.translateRepository = translateRepository;
    this.supportedLanguagesRepository = supportedLanguagesRepository;
  }

  // ------------------------------------- public methods -----------------------------------------

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

  public void run(final Callbacks callbacks) {
    if (callbacks == null) {
      throw new IllegalStateException("Callbacks in UseCase must be non null");
    }

    RepositoryAsyncTaskResponse.PostExecuteListenerResponse<Translation> getTranslationListener =
        new RepositoryAsyncTaskResponse.PostExecuteListenerResponse<Translation>() {
          @Override
          public void onResult(Translation translation) {
            LanguagePair languagePair = supportedLanguagesRepository.getLanguages(
                translation.getLanguageCodes()[0],
                translation.getLanguageCodes()[1]);
              callbacks.onGetLastTranslationSuccess(new Pair<>(translation, languagePair));
          }

          @Override
          public void onException(ExceptionBundle exception) {
            LanguagePair languagePair = supportedLanguagesRepository.getDefaultLanguages();
            callbacks.onGetLastTranslationException(new Pair<>(exception, languagePair));
          }
        };

    RepositoryAsyncTaskResponse.RepositoryRunnableResponse<Translation> getTranslationRunnable =
        new RepositoryAsyncTaskResponse.RepositoryRunnableResponse<Translation>() {
          @Override
          public Translation run() throws ExceptionBundle {
            return translateRepository.getLastTranslation();
          }
        };

    getTranslation = new RepositoryAsyncTaskResponse<>(
        getTranslationRunnable,
        getTranslationListener);
    getTranslation.executeOnExecutor(executor);
  }

  // ------------------------------------ inner types --------------------------------------------

  public interface Callbacks {

    void onGetLastTranslationSuccess(Pair<Translation, LanguagePair> result);

    void onGetLastTranslationException(Pair<ExceptionBundle, LanguagePair> exception);

  }
}
