package com.danielkashin.yandextestapplication.data_layer.services.base;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
* Retrofit one love
* generic service is helpful if out application will become bigger and ther services will be required
*/
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


  protected S getService() {
    return this.service;
  }


  private void bindService() {
    this.service = createService(this.retrofit);
  }


  protected abstract S createService(Retrofit retrofit);

}
