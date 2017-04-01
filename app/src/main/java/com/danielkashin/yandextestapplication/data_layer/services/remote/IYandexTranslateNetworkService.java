package com.danielkashin.yandextestapplication.data_layer.services.remote;

import com.danielkashin.yandextestapplication.data_layer.entities.remote.Translation;

import retrofit2.Call;

public interface IYandexTranslateNetworkService {

  public Call<Translation> translate(String text, String lang);

}
