package com.danielkashin.yandextestapplication.domain_layer.async_task;

import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;

/*
* generic asynctask is acceptable and elegant way to solve out problem:
* no exceptions different from ExceptionBundle must be here,
* as in UseCases we get data through the repository
* asynctask also supports easy cancelling
*/
public class RepositoryAsyncTaskVoid extends VoidAsyncTask<ExceptionBundle> {

  private final RepositoryRunnableVoid repositoryRunnable;
  private final PostExecuteListenerVoid postExecuteListener;


  public RepositoryAsyncTaskVoid(RepositoryRunnableVoid repositoryRunnable,
                                 PostExecuteListenerVoid postExecuteListener) {
    this.repositoryRunnable = repositoryRunnable;
    this.postExecuteListener = postExecuteListener;
  }

  // --------------------------------------- AsyncTask --------------------------------------------

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

  // ------------------------------------ inner types ---------------------------------------------

  public interface PostExecuteListenerVoid {

    void onResult();

    void onException(ExceptionBundle exception);

  }

  public interface RepositoryRunnableVoid {

    void run() throws ExceptionBundle;

  }
}
