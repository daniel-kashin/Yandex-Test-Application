package com.danielkashin.yandextestapplication.domain_layer.async_task;

import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;


public class RepositoryVoidAsyncTask<T> extends VoidAsyncTask<ExceptionBundle> {

  private final RepositoryRunnable repositoryRunnable;
  private final PostExecuteListener postExecuteListener;


  public RepositoryVoidAsyncTask(RepositoryRunnable repositoryRunnable,
                                     PostExecuteListener postExecuteListener) {
    this.repositoryRunnable = repositoryRunnable;
    this.postExecuteListener = postExecuteListener;
  }


  @Override
  protected void onCancelled() {
    // do nothing
  }

  @Override
  protected ExceptionBundle doInBackground(Void... params) {
    try {
      repositoryRunnable.run();
      return null;
    } catch (ExceptionBundle exception) {
      return exception;
    }
  }

  @Override
  protected void onPostExecute(ExceptionBundle exception) {
    super.onPostExecute(exception);
    if (postExecuteListener != null && !isCancelled()) {
      if (exception != null && !isCancelled()) {
        postExecuteListener.onException(exception);
      } else if (!isCancelled()) {
        postExecuteListener.onResult();
      }
    }
  }


  public interface PostExecuteListener {

    void onResult();

    void onException(ExceptionBundle exception);

  }

  public interface RepositoryRunnable {

    void run() throws ExceptionBundle;

  }
}
