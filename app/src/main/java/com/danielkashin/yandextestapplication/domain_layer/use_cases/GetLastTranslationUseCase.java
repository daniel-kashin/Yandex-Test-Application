package com.danielkashin.yandextestapplication.domain_layer.use_cases;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryResponseAsyncTask;
import com.danielkashin.yandextestapplication.domain_layer.pojo.LanguagePair;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.danielkashin.yandextestapplication.domain_layer.repository.supported_languages.ISupportedLanguagesRepository;
import com.danielkashin.yandextestapplication.domain_layer.repository.translate.ITranslateRepository;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.base.IUseCase;
import java.util.concurrent.Executor;


public class GetLastTranslationUseCase implements IUseCase {

  @NonNull
  private final Executor executor;
  @NonNull
  private final ITranslateRepository translateRepository;
  @NonNull
  private final ISupportedLanguagesRepository supportedLanguagesRepository;

  private RepositoryResponseAsyncTask<Translation> getLastTranslationAsyncTask;


  public GetLastTranslationUseCase(@NonNull Executor executor,
                                   @NonNull ITranslateRepository translateRepository,
                                   @NonNull ISupportedLanguagesRepository supportedLanguagesRepository) {
    this.executor = executor;
    this.translateRepository = translateRepository;
    this.supportedLanguagesRepository = supportedLanguagesRepository;
  }

  // --------------------------------------- IUseCase ---------------------------------------------

  @Override
  public void cancel() {
    if (isRunning()) {
      getLastTranslationAsyncTask.cancel(false);
    }
  }

  // ------------------------------------- public methods -----------------------------------------

  public void run(final Callbacks callbacks) {
    RepositoryResponseAsyncTask.PostExecuteListener<Translation> listener =
        new RepositoryResponseAsyncTask.PostExecuteListener<Translation>() {
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

    RepositoryResponseAsyncTask.RepositoryRunnable<Translation> repositoryRunnable =
        new RepositoryResponseAsyncTask.RepositoryRunnable<Translation>() {
          @Override
          public Translation run() throws ExceptionBundle {
            return translateRepository.getLastTranslation();
          }
        };

    getLastTranslationAsyncTask = new RepositoryResponseAsyncTask<>(
        repositoryRunnable,
        listener);
    getLastTranslationAsyncTask.executeOnExecutor(executor);
  }

  public boolean isRunning() {
    return getLastTranslationAsyncTask != null
        && getLastTranslationAsyncTask.getStatus() == AsyncTask.Status.RUNNING;
  }

  // ------------------------------------ inner classes--------------------------------------------

  public interface Callbacks {

    void onGetLastTranslationSuccess(Pair<Translation, LanguagePair> result);

    void onGetLastTranslationException(Pair<ExceptionBundle, LanguagePair> exception);

  }
}
