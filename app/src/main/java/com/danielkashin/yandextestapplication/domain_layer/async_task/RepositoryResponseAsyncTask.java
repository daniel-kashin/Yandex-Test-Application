package com.danielkashin.yandextestapplication.domain_layer.async_task;

import android.util.Pair;

import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;


public class RepositoryResponseAsyncTask<T> extends VoidAsyncTask<Pair<T, ExceptionBundle>> {

  private final RepositoryRunnable<T> repositoryRunnable;
  private final PostExecuteListener<T> postExecuteListener;


  public RepositoryResponseAsyncTask(RepositoryRunnable<T> repositoryRunnable,
                                     PostExecuteListener<T> postExecuteListener) {
    this.repositoryRunnable = repositoryRunnable;
    this.postExecuteListener = postExecuteListener;
  }


  @Override
  protected void onCancelled() {
    // do nothing
  }

  @Override
  protected Pair<T, ExceptionBundle> doInBackground(Void... params) {
    try {
      T result = repositoryRunnable.run();
      return new Pair<>(result, null);
    } catch (ExceptionBundle exception) {
      return new Pair<>(null, exception);
    }
  }

  @Override
  protected void onPostExecute(Pair<T, ExceptionBundle> result) {
    super.onPostExecute(result);
    if (postExecuteListener != null && !isCancelled()) {
      if (result.second != null && !isCancelled()) {
        postExecuteListener.onException(result.second);
      } else if (!isCancelled()) {
        postExecuteListener.onResult(result.first);
      }
    }
  }


  public interface PostExecuteListener<T> {

    void onResult(T result);

    void onException(ExceptionBundle exception);

  }

  public interface RepositoryRunnable<T> {

    T run() throws ExceptionBundle;

  }
}
