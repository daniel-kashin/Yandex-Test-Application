package com.danielkashin.yandextestapplication.domain_layer.use_cases.remote;

import android.os.AsyncTask;
import android.util.Pair;

import com.danielkashin.yandextestapplication.data_layer.entities.remote.NetworkTranslation;
import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.data_layer.services.remote.ITranslateRemoteService;
import com.danielkashin.yandextestapplication.domain_layer.async_task.remote.NetworkAsyncTask;
import com.danielkashin.yandextestapplication.domain_layer.async_task.remote.VoidAsyncTask;
import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;
import com.danielkashin.yandextestapplication.domain_layer.repository.GetTranslationCallback;
import com.danielkashin.yandextestapplication.domain_layer.repository.ITranslateRepository;
import com.danielkashin.yandextestapplication.domain_layer.repository.TranslateRepository;
import com.danielkashin.yandextestapplication.domain_layer.use_cases.base.UseCase;

import java.util.concurrent.Executor;


public class TranslateUseCase implements UseCase {

  private final Executor executor;
  private final ITranslateRepository repository;
  private VoidAsyncTask getTranslationAsyncTask;


  public TranslateUseCase(Executor executor, ITranslateRepository repository) {
    this.executor = executor;
    this.repository = repository;
  }

  @Override
  public void cancel() {
    if (isRunning()){
      getTranslationAsyncTask.cancel(false);
    }
  }

  public void run(final Callbacks callbacks, final String originalText, final String language) {
    GetTranslationCallback getTranslationCallback = new GetTranslationCallback() {
      @Override
      public void onResult(Translation translation) {
        callbacks.onTranslateSuccess(translation);
      }

      @Override
      public void onError(Exception exception) {
        if (exception instanceof ExceptionBundle) {
          callbacks.onTranslateError(new Pair<>(originalText, (ExceptionBundle)exception));
        } else {
          callbacks.onTranslateError(new Pair<>(originalText, new ExceptionBundle(ExceptionBundle.Reason.UNKNOWN)));
        }
      }
    };

    getTranslationAsyncTask = repository.getTranslation(originalText, language, getTranslationCallback);
    getTranslationAsyncTask.executeOnExecutor(executor);
  }

  public boolean isRunning() {
    return getTranslationAsyncTask != null
        && getTranslationAsyncTask.getStatus() == AsyncTask.Status.RUNNING;
  }


  public interface Callbacks {

    void onTranslateSuccess(Translation result);

    void onTranslateError(Pair<String, ExceptionBundle> pairOriginalTextException);

  }

}
