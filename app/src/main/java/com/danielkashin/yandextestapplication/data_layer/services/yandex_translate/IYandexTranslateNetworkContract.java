package com.danielkashin.yandextestapplication.data_layer.services.yandex_translate;


import com.danielkashin.yandextestapplication.data_layer.entitles.yandex_translate.Translation;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

import static com.danielkashin.yandextestapplication.data_layer.constants.Endpoints.API;
import static com.danielkashin.yandextestapplication.data_layer.constants.Endpoints.GET_LANGS;
import static com.danielkashin.yandextestapplication.data_layer.constants.Endpoints.TRANSLATE;
import static com.danielkashin.yandextestapplication.data_layer.constants.Endpoints.TR_JSON;
import static com.danielkashin.yandextestapplication.data_layer.constants.Endpoints.V1_5;

public interface IYandexTranslateNetworkContract {

  @POST(API + V1_5 + TR_JSON + GET_LANGS)
  Call<ResponseBody> getLangs(
      @Query("key") String key,
      @Query("ui") String ui
  );


  @POST(API + V1_5 + TR_JSON + TRANSLATE)
  Call<Translation> translate(
      @Query("key") String key,
      @Query("text") String text,
      @Query("lang") String lang
  );

}
