package com.danielkashin.yandextestapplication.domain_layer.async_task;


import android.os.AsyncTask;
import android.util.Pair;

import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Response;



public class NetworkAsyncTask<T> extends AsyncTask<Void, Void, Pair<T, ExceptionBundle>> {

  private Call<T> mApiCall;
  private PostExecuteListener<T> postExecuteListener;

  public NetworkAsyncTask(Call<T> apiCall,
                        PostExecuteListener<T> postExecuteListener) {
    this.mApiCall = apiCall;
    this.postExecuteListener = postExecuteListener;
  }

  @Override
  protected void onCancelled() {
    // do nothing
  }

  @Override
  protected Pair<T, ExceptionBundle> doInBackground(Void... params) {
    try {
      Response<T> response = mApiCall.execute();

      T responseBody = response.body();

      if (response.code() == 200 && responseBody != null) {
        return new Pair<>(responseBody, null);
      } else {
        return new Pair<>(null, new ExceptionBundle(ExceptionBundle.Reason.NO_SESSION));
      }
    } catch (ConnectException | SocketTimeoutException e) {
      return new Pair<>(null, new ExceptionBundle(ExceptionBundle.Reason.NETWORK_UNAVAILABLE));
    } catch (IOException e) {
      return new Pair<>(null, new ExceptionBundle(ExceptionBundle.Reason.NO_SESSION));
    }
  }

  @Override
  protected void onPostExecute(Pair<T, ExceptionBundle> result) {
    super.onPostExecute(result);
    if (postExecuteListener != null && !isCancelled()){
      if (result.first != null && !isCancelled()) {
        postExecuteListener.onResult(result.first);
      } else if (!isCancelled()) {
        postExecuteListener.onError(result.second);
      }
    }
  }


  public interface PostExecuteListener<T> {

    void onResult(T result);

    void onError(ExceptionBundle error);

  }
}
