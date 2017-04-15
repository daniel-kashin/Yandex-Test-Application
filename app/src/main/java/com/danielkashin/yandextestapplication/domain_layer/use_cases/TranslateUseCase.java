package com.danielkashin.yandextestapplication.domain_layer.use_cases;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryResponseAsyncTask;
import com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryVoidAsyncTask;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.danielkashin.yandextestapplication.domain_layer.repository.translate.ITranslateRepository;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.base.IUseCase;
import java.util.concurrent.Executor;


public class TranslateUseCase implements IUseCase {

  @NonNull
  private final Executor executor;
  @NonNull
  private final ITranslateRepository repository;

  private RepositoryResponseAsyncTask<Translation> getTranslationAsyncTask;


  public TranslateUseCase(@NonNull Executor executor, @NonNull ITranslateRepository repository) {
    this.executor = executor;
    this.repository = repository;
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

  public void run(final Callbacks uiCallbacks, final String originalText, final String language) {
    RepositoryResponseAsyncTask.PostExecuteListener<Translation> listener =
        new RepositoryResponseAsyncTask.PostExecuteListener<Translation>() {
          @Override
          public void onResult(final Translation result) {
            // notify UI that translation has finished successfully
            uiCallbacks.onTranslateSuccess(result);

            // save translation right after getting it
            RepositoryVoidAsyncTask.RepositoryRunnable saveTranslationRunnable =
                new RepositoryVoidAsyncTask.RepositoryRunnable() {
                  @Override
                  public void run() throws ExceptionBundle {
                    repository.saveTranslation(result);
                  }
                };


            // don`t save this asynctask cause it`s not needed to cancel it later
            new RepositoryVoidAsyncTask<>(
                saveTranslationRunnable,
                null                      // don`t want to notify user about saving
            ).executeOnExecutor(executor);
          }

          @Override
          public void onException(ExceptionBundle exception) {
            // notify UI that error occured
            uiCallbacks.onTranslateException(new Pair<>(exception, originalText));
          }
        };

    // wrap call to repository into the custom runnable
    RepositoryResponseAsyncTask.RepositoryRunnable<Translation> runnable =
        new RepositoryResponseAsyncTask.RepositoryRunnable<Translation>() {
          @Override
          public Translation run() throws ExceptionBundle {
            return repository.getTranslation(originalText, language);
          }
        };

    // execute call to repository async
    getTranslationAsyncTask = new RepositoryResponseAsyncTask<>(
        runnable,
        listener
    );
    getTranslationAsyncTask.executeOnExecutor(executor);
  }

  public boolean isRunning() {
    return getTranslationAsyncTask != null
        && getTranslationAsyncTask.getStatus() == AsyncTask.Status.RUNNING
        && !getTranslationAsyncTask.isCancelled();
  }


  // ------------------------------------ callbacks ----------------------------------------------

  public interface Callbacks {

    void onTranslateSuccess(Translation result);

    void onTranslateException(Pair<ExceptionBundle, String> result);

  }
}
