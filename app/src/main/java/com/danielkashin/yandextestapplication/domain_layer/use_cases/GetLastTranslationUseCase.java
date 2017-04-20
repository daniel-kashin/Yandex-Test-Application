package com.danielkashin.yandextestapplication.domain_layer.use_cases;

import android.os.AsyncTask;
import android.support.v4.util.Pair;
import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskResponse;
import com.danielkashin.yandextestapplication.domain_layer.pojo.LanguagePair;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.danielkashin.yandextestapplication.domain_layer.repository.languages.ILanguagesRepository;
import com.danielkashin.yandextestapplication.domain_layer.repository.translate.ITranslationsRepository;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.base.IUseCase;
import java.util.concurrent.Executor;


public class GetLastTranslationUseCase implements IUseCase {

  private final Executor executor;
  private final ITranslationsRepository translateRepository;
  private final ILanguagesRepository supportedLanguagesRepository;

  private RepositoryAsyncTaskResponse<Translation> getLastTranslationAsyncTask;


  public GetLastTranslationUseCase(Executor executor,
                                   ITranslationsRepository translateRepository,
                                   ILanguagesRepository supportedLanguagesRepository) {
    if (executor == null || translateRepository == null || supportedLanguagesRepository == null){
      throw new IllegalArgumentException("All arguments of use case must be non null");
    }

    this.executor = executor;
    this.translateRepository = translateRepository;
    this.supportedLanguagesRepository = supportedLanguagesRepository;
  }

  // --------------------------------------- IUseCase ---------------------------------------------

  @Override
  public void cancel() {
    if (isRunning()) {
      getLastTranslationAsyncTask.cancel(false);
      getLastTranslationAsyncTask = null;
    }
  }

  // ------------------------------------- public methods -----------------------------------------

  public boolean isRunning() {
    return getLastTranslationAsyncTask != null
        && getLastTranslationAsyncTask.getStatus() == AsyncTask.Status.RUNNING
        && !getLastTranslationAsyncTask.isCancelled();
  }


  public void run(final Callbacks callbacks) {
    RepositoryAsyncTaskResponse.PostExecuteListenerResponse<Translation> listener =
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

    RepositoryAsyncTaskResponse.RepositoryRunnableResponse<Translation> repositoryRunnable =
        new RepositoryAsyncTaskResponse.RepositoryRunnableResponse<Translation>() {
          @Override
          public Translation run() throws ExceptionBundle {
            return translateRepository.getLastTranslation();
          }
        };

    getLastTranslationAsyncTask = new RepositoryAsyncTaskResponse<>(
        repositoryRunnable,
        listener);
    getLastTranslationAsyncTask.executeOnExecutor(executor);
  }

  // ------------------------------------ inner classes--------------------------------------------

  public interface Callbacks {

    void onGetLastTranslationSuccess(Pair<Translation, LanguagePair> result);

    void onGetLastTranslationException(Pair<ExceptionBundle, LanguagePair> exception);

  }
}
