package com.danielkashin.yandextestapplication.domain_layer.async_task;

import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;


public class RepositoryAsyncTaskVoid<T> extends VoidAsyncTask<ExceptionBundle> {

  private final RepositoryRunnableVoid repositoryRunnable;
  private final PostExecuteListenerVoid postExecuteListener;


  public RepositoryAsyncTaskVoid(RepositoryRunnableVoid repositoryRunnable,
                                 PostExecuteListenerVoid postExecuteListener) {
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


  public interface PostExecuteListenerVoid {

    void onResult();

    void onException(ExceptionBundle exception);

  }

  public interface RepositoryRunnableVoid {

    void run() throws ExceptionBundle;

  }
}
