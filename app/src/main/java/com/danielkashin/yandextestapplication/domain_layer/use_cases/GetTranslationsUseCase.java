package com.danielkashin.yandextestapplication.domain_layer.use_cases;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.domain_layer.async_task.RepositoryResponseAsyncTask;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.danielkashin.yandextestapplication.domain_layer.repository.translate.ITranslateRepository;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.base.IUseCase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;


public class GetTranslationsUseCase implements IUseCase {

  @NonNull
  private final Executor executor;
  @NonNull
  private final ITranslateRepository translateRepository;
  private final boolean onlyFavorite;

  private RepositoryResponseAsyncTask<ArrayList<Translation>> getTranslationsAsyncTask;


  public GetTranslationsUseCase(@NonNull Executor executor,
                                @NonNull ITranslateRepository translateRepository,
                                boolean onlyFavorite){
    this.executor = executor;
    this.translateRepository = translateRepository;
    this.onlyFavorite = onlyFavorite;
  }

  // --------------------------------------- IUseCase ---------------------------------------------

  @Override
  public void cancel() {
    if (isRunning()){
      getTranslationsAsyncTask.cancel(false);
      getTranslationsAsyncTask = null;
    }
  }

  // ------------------------------------- public methods -----------------------------------------

  public void run(final Callbacks callbacks, final int offset,
                  final int count, final String searchRequest){
    RepositoryResponseAsyncTask.PostExecuteListener<ArrayList<Translation>> listener =
        new RepositoryResponseAsyncTask.PostExecuteListener<ArrayList<Translation>>() {
          @Override
          public void onResult(ArrayList<Translation> result) {
            callbacks.onGetTranslationsSuccess(result, offset, searchRequest);
          }

          @Override
          public void onException(ExceptionBundle exception) {
            callbacks.onGetTranslationsException(exception, offset, searchRequest);
          }
        };

    RepositoryResponseAsyncTask.RepositoryRunnable<ArrayList<Translation>> runnable =
        new RepositoryResponseAsyncTask.RepositoryRunnable<ArrayList<Translation>>() {
          @Override
          public ArrayList<Translation> run() throws ExceptionBundle {
            return translateRepository.getTranslations(offset, count, onlyFavorite, searchRequest);
          }
        };

    getTranslationsAsyncTask = new RepositoryResponseAsyncTask<>(
        runnable,
        listener
    );
    getTranslationsAsyncTask.executeOnExecutor(executor);
  }

  public boolean isRunning(){
    return getTranslationsAsyncTask != null
        && getTranslationsAsyncTask.getStatus() == AsyncTask.Status.RUNNING
        && !getTranslationsAsyncTask.isCancelled();
  }

  // ------------------------------------ inner classes--------------------------------------------

  public interface Callbacks {

    void onGetTranslationsSuccess(ArrayList<Translation> translations, int offset, String searchRequest);

    void onGetTranslationsException(ExceptionBundle exceptionBundle, int offset, String searchRequest);

  }
}
