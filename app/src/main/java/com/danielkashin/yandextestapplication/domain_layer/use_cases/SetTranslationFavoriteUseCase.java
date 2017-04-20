package com.danielkashin.yandextestapplication.domain_layer.use_cases;

import android.os.AsyncTask;
import android.support.v4.util.Pair;

import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskResponse;
import com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskVoid;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.danielkashin.yandextestapplication.domain_layer.repository.translate.ITranslationsRepository;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.base.IUseCase;
import static com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskVoid.RepositoryRunnableVoid;
import static com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryAsyncTaskVoid.PostExecuteListenerVoid;

import java.util.concurrent.Executor;


public class SetTranslationFavoriteUseCase implements IUseCase {

  private final Executor executor;
  private final ITranslationsRepository translateRepository;

  private RepositoryAsyncTaskVoid setTranslationFavoriteAsyncTask;


  public SetTranslationFavoriteUseCase(Executor executor, ITranslationsRepository translateRepository) {
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
      setTranslationFavoriteAsyncTask.cancel(false);
      setTranslationFavoriteAsyncTask = null;
    }
  }

  // -------------------------------------- public methods ----------------------------------------

  public boolean isRunning() {
    return setTranslationFavoriteAsyncTask != null
        && setTranslationFavoriteAsyncTask.getStatus() == AsyncTask.Status.RUNNING
        && !setTranslationFavoriteAsyncTask.isCancelled();
  }

  public void run(final Callbacks uiCallbacks, final String originalText, final String translatedText,
                  final String languageCodePair, final boolean favorite) {
    RepositoryRunnableVoid setTranslationRunnable = new RepositoryRunnableVoid() {
      @Override
      public void run() throws ExceptionBundle {
        Translation translation = new Translation(originalText, translatedText, languageCodePair, favorite);
        translateRepository.saveTranslation(translation);
      }
    };

    PostExecuteListenerVoid setTranslationListener = new PostExecuteListenerVoid() {
      @Override
      public void onResult() {
        uiCallbacks.onSetTranslationFavoriteSuccess();
      }

      @Override
      public void onException(ExceptionBundle exception) {
        uiCallbacks.onSetTranslationFavoriteException(exception);
      }
    };

    setTranslationFavoriteAsyncTask = new RepositoryAsyncTaskVoid(setTranslationRunnable, setTranslationListener);
    setTranslationFavoriteAsyncTask.executeOnExecutor(executor);
  }

  // ------------------------------------ callbacks ----------------------------------------------

  public interface Callbacks {

    void onSetTranslationFavoriteSuccess();

    void onSetTranslationFavoriteException(ExceptionBundle exceptionBundle);

  }

}
