package com.danielkashin.yandextestapplication.data_layer.services.base;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public abstract class NetworkService<S> {

  private Retrofit retrofit;

  private S service;

  public NetworkService(String baseUrl, OkHttpClient okHttpClient){
    this.retrofit = new Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build();

    bindService();
  }

  private void bindService() {
    this.service = createService(this.retrofit);
  }

  protected abstract S createService(Retrofit retrofit);

  protected S getService() {
    return this.service;
  }

}
