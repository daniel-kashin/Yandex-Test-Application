package com.danielkashin.yandextestapplication.data_layer.services.remote;

import com.danielkashin.yandextestapplication.data_layer.entities.remote.NetworkTranslation;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

import static com.danielkashin.yandextestapplication.data_layer.constants.Endpoints.API;
import static com.danielkashin.yandextestapplication.data_layer.constants.Endpoints.TRANSLATE;
import static com.danielkashin.yandextestapplication.data_layer.constants.Endpoints.TR_JSON;
import static com.danielkashin.yandextestapplication.data_layer.constants.Endpoints.V1_5;


interface ITranslateRemoteContract {

  @POST(API + V1_5 + TR_JSON + TRANSLATE)
  Call<NetworkTranslation> translate(
      @Query("key") String key,
      @Query("text") String text,
      @Query("lang") String lang
  );

}
