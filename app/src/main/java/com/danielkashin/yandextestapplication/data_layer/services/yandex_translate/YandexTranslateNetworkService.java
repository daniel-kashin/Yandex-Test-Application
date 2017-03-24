package com.danielkashin.yandextestapplication.data_layer.services.yandex_translate;


import com.danielkashin.yandextestapplication.data_layer.constants.Endpoints;
import com.danielkashin.yandextestapplication.data_layer.services.base.NetworkService;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public class YandexTranslateNetworkService extends NetworkService<IYandexTranslateNetworkService>
    implements IYandexTranslateNetworkService {

  private YandexTranslateNetworkService(OkHttpClient okHttpClient){
    super(Endpoints.YANDEX_TRANSLATE_BASE_URL, okHttpClient);
  }

  @Override
  protected IYandexTranslateNetworkService createService(Retrofit retrofit) {
    return retrofit.create(IYandexTranslateNetworkService.class);
  }

  // -------------------------- IYandexTranslateNetworkService methods -----------------------------



  // -----------------------------------------------------------------------------------------------

  public static final class Factory {

    private Factory() {
      // "static class" in Java
    }

    public static IYandexTranslateNetworkService create(OkHttpClient okHttpClient) {
      return new YandexTranslateNetworkService(okHttpClient);
    }
  }
}
