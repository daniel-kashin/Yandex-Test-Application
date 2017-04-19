package com.danielkashin.yandextestapplication.domain_layer.use_cases;

import android.os.AsyncTask;
import android.support.v4.util.Pair;

import com.danielkashin.yandextestapplication.BuildConfig;
import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryResponseAsyncTask;
import com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryVoidAsyncTask;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.danielkashin.yandextestapplication.domain_layer.repository.translate.ITranslationsRepository;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.base.IUseCase;

import java.util.concurrent.Executor;


public class TranslateUseCase implements IUseCase {

  private final Executor executor;
  private final ITranslationsRepository translateRepository;

  private RepositoryResponseAsyncTask<Pair<Translation, Boolean>> getTranslationAsyncTask;


  public TranslateUseCase(Executor executor, ITranslationsRepository translateRepository) {
    if (executor == null || translateRepository == null) {
      throw new IllegalArgumentException("All arguments of use case must be non null");
    }

    this.executor = executor;
    this.translateRepository = translateRepository;
  }

  // ---------------------------------------- IUseCase --------------------------------------------

  @Override
  public void cancel() {
    if (isRunning()) {
      getTranslationAsyncTask.cancel(false);
      getTranslationAsyncTask = null;
    }
  }

  // -------------------------------------- public methods ----------------------------------------

  public boolean isRunning() {
    return getTranslationAsyncTask != null
        && getTranslationAsyncTask.getStatus() == AsyncTask.Status.RUNNING
        && !getTranslationAsyncTask.isCancelled();
  }

  public void run(final Callbacks uiCallbacks, final String originalText, final String language) {
    RepositoryResponseAsyncTask.PostExecuteListener<Pair<Translation, Boolean>> listener =
        new RepositoryResponseAsyncTask.PostExecuteListener<Pair<Translation, Boolean>>() {
          @Override
          public void onResult(final Pair<Translation, Boolean> result) {
            // notify UI that translation has finished successfully
            if (uiCallbacks != null) {
              uiCallbacks.onTranslateSuccess(result.first);
            }

            if (result.second) {
              // save translation right after getting it
              RepositoryVoidAsyncTask.RepositoryRunnable saveTranslationRunnable =
                  new RepositoryVoidAsyncTask.RepositoryRunnable() {
                    @Override
                    public void run() throws ExceptionBundle {
                      translateRepository.saveTranslation(result.first);
                    }
                  };
              RepositoryVoidAsyncTask.PostExecuteListener innerListener
                  = new RepositoryVoidAsyncTask.PostExecuteListener() {
                @Override
                public void onResult() {
                  if (uiCallbacks != null) uiCallbacks.onSaveTranslationSuccess();

                }

                @Override
                public void onException(ExceptionBundle exception) {
                  if (uiCallbacks != null) uiCallbacks.onSaveTranslationException(exception);
                }
              };

              new RepositoryVoidAsyncTask<>(saveTranslationRunnable, innerListener).executeOnExecutor(executor);
            }
          }

          @Override
          public void onException(ExceptionBundle exception) {
            uiCallbacks.onTranslateException(new Pair<>(exception, originalText));
          }
        };

    // wrap call to translateRepository into the custom runnable
    RepositoryResponseAsyncTask.RepositoryRunnable<Pair<Translation, Boolean>> runnable =
        new RepositoryResponseAsyncTask.RepositoryRunnable<Pair<Translation, Boolean>>() {
          @Override
          public Pair<Translation, Boolean> run() throws ExceptionBundle {
            return translateRepository.getTranslationAndItsSource(originalText, language);
          }
        };

    // execute call to translateRepository async
    getTranslationAsyncTask = new RepositoryResponseAsyncTask<>(
        runnable,
        listener
    );
    getTranslationAsyncTask.executeOnExecutor(executor);
  }

  // ------------------------------------ callbacks ----------------------------------------------

  public interface Callbacks {

    void onSaveTranslationSuccess();

    void onSaveTranslationException(ExceptionBundle exceptionBundle);

    void onTranslateSuccess(Translation result);

    void onTranslateException(Pair<ExceptionBundle, String> result);

  }
}
