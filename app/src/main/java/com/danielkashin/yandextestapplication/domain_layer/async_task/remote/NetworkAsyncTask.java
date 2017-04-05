package com.danielkashin.yandextestapplication.domain_layer.async_task.remote;

import android.os.AsyncTask;
import android.util.Pair;

import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import retrofit2.Call;
import retrofit2.Response;



public class NetworkAsyncTask<T> extends VoidAsyncTask<Pair<T, ExceptionBundle>> {

  private final Call<T> mApiCall;
  private final PostExecuteListener<T> postExecuteListener;


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
      } else if (response.code() == 401) {
        return new Pair<>(null, new ExceptionBundle(ExceptionBundle.Reason.WRONG_KEY));
      } else if (response.code() == 404) {
        return new Pair<>(null, new ExceptionBundle(ExceptionBundle.Reason.LIMIT_EXPIRED));
      } else if (response.code() == 413 || response.code() == 414) {
        return new Pair<>(null, new ExceptionBundle(ExceptionBundle.Reason.TEXT_LIMIT_EXPIRED));
      } else if (response.code() == 422) {
        return new Pair<>(null, new ExceptionBundle(ExceptionBundle.Reason.WRONG_TEXT));
      } else if (response.code() == 501) {
        return new Pair<>(null, new ExceptionBundle(ExceptionBundle.Reason.WRONG_LANGS));
      } else {
        throw new IOException();
      }
    } catch (ConnectException | SocketTimeoutException | UnknownHostException | SSLException e) {
      return new Pair<>(null, new ExceptionBundle(ExceptionBundle.Reason.NETWORK_UNAVAILABLE));
    } catch (IOException e) {
      return new Pair<>(null, new ExceptionBundle(ExceptionBundle.Reason.UNKNOWN));
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
