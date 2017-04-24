package com.danielkashin.yandextestapplication.domain_layer.use_cases;

import android.os.AsyncTask;
import android.support.v4.util.Pair;

import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskResponse;
import com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskVoid;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.danielkashin.yandextestapplication.data_layer.repository.translate.ITranslationsRepository;

import static com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskResponse.RepositoryRunnableResponse;
import static com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskVoid.RepositoryRunnableVoid;
import static com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskResponse.PostExecuteListenerResponse;
import static com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskVoid.PostExecuteListenerVoid;

import java.util.concurrent.Executor;


public class TranslateUseCase {

  private final Executor executor;
  private final ITranslationsRepository translateRepository;

  private RepositoryAsyncTaskResponse<Pair<Translation, Translation.Source>> getTranslation;


  public TranslateUseCase(Executor executor, ITranslationsRepository translateRepository) {
    if (executor == null || translateRepository == null) {
      throw new IllegalArgumentException("All arguments of use case must be non null");
    }

    this.executor = executor;
    this.translateRepository = translateRepository;
  }

  // -------------------------------------- public methods ----------------------------------------

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

  public void run(final Callbacks uiCallbacks, final String originalText, final String language) {
    PostExecuteListenerResponse<Pair<Translation, Translation.Source>> translateListener =
        new PostExecuteListenerResponse<Pair<Translation, Translation.Source>>() {
          @Override
          public void onException(ExceptionBundle exception) {
            uiCallbacks.onTranslateException(new Pair<>(exception, originalText));
          }

          @Override
          public void onResult(final Pair<Translation, Translation.Source> result) {
            uiCallbacks.onTranslateSuccess(result.first);

            if (result.second == Translation.Source.LOCAL) {
              uiCallbacks.onSaveTranslationAfterGettingSuccess();
            } else if (result.second == Translation.Source.REMOTE) {
              RepositoryRunnableVoid saveTranslationRunnable = new RepositoryRunnableVoid() {
                @Override
                public void run() throws ExceptionBundle {
                  translateRepository.saveTranslation(result.first);
                }
              };

              PostExecuteListenerVoid saveTranslationListener = new PostExecuteListenerVoid() {
                @Override
                public void onResult() {
                  uiCallbacks.onSaveTranslationAfterGettingSuccess();
                }

                @Override
                public void onException(ExceptionBundle exception) {
                  uiCallbacks.onSaveTranslationAfterGettingException(exception);
                }
              };

              new RepositoryAsyncTaskVoid<>(saveTranslationRunnable, saveTranslationListener)
                  .executeOnExecutor(executor);
            }
          }
        };

    RepositoryRunnableResponse<Pair<Translation, Translation.Source>> translateRunnable =
        new RepositoryRunnableResponse<Pair<Translation, Translation.Source>>() {
          @Override
          public Pair<Translation, Translation.Source> run() throws ExceptionBundle {
            return translateRepository.getTranslationAndItsSource(originalText, language);
          }
        };


    getTranslation = new RepositoryAsyncTaskResponse<>(translateRunnable, translateListener);
    getTranslation.executeOnExecutor(executor);
  }

  // ------------------------------------ callbacks ----------------------------------------------

  public interface Callbacks {

    void onSaveTranslationAfterGettingSuccess();

    void onSaveTranslationAfterGettingException(ExceptionBundle exceptionBundle);

    void onTranslateSuccess(Translation result);

    void onTranslateException(Pair<ExceptionBundle, String> result);

  }
}
