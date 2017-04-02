package com.danielkashin.yandextestapplication.data_layer.services.remote;

import com.danielkashin.yandextestapplication.data_layer.entities.remote.NetworkTranslation;

import retrofit2.Call;

public interface ITranslateNetworkService {

  Call<NetworkTranslation> translate(String text, String lang);

}
