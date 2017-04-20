package com.danielkashin.yandextestapplication.domain_layer.async_task;

import android.util.Pair;

import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;


public class RepositoryAsyncTaskResponse<T> extends VoidAsyncTask<Pair<T, ExceptionBundle>> {

  private final RepositoryRunnableResponse<T> repositoryRunnable;
  private final PostExecuteListenerResponse<T> postExecuteListener;


  public RepositoryAsyncTaskResponse(RepositoryRunnableResponse<T> repositoryRunnable,
                                     PostExecuteListenerResponse<T> postExecuteListener) {
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
    if (!isCancelled() && postExecuteListener != null && result.second != null) {
      postExecuteListener.onException(result.second);
    } else if (!isCancelled() && postExecuteListener != null) {
      postExecuteListener.onResult(result.first);
    }
  }


  public interface PostExecuteListenerResponse<T> {

    void onResult(T result);

    void onException(ExceptionBundle exception);

  }

  public interface RepositoryRunnableResponse<T> {

    T run() throws ExceptionBundle;

  }
}
