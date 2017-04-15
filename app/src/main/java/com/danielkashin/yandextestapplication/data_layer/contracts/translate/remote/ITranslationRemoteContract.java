package com.danielkashin.yandextestapplication.data_layer.contracts.translate.remote;

import com.danielkashin.yandextestapplication.data_layer.entities.translate.remote.NetworkTranslation;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;
import static com.danielkashin.yandextestapplication.data_layer.constants.Endpoints.KEY_API;
import static com.danielkashin.yandextestapplication.data_layer.constants.Endpoints.KEY_TRANSLATE;
import static com.danielkashin.yandextestapplication.data_layer.constants.Endpoints.KEY_TR_JSON;
import static com.danielkashin.yandextestapplication.data_layer.constants.Endpoints.KEY_V1_5;


public interface ITranslationRemoteContract {

  @POST(KEY_API + KEY_V1_5 + KEY_TR_JSON + KEY_TRANSLATE)
  Call<NetworkTranslation> translate(
      @Query("key") String key,
      @Query("text") String text,
      @Query("lang") String lang
  );

}
