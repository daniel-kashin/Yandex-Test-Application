package com.danielkashin.yandextestapplication.data_layer.services.remote;

import com.danielkashin.yandextestapplication.BuildConfig;
import com.danielkashin.yandextestapplication.data_layer.constants.Endpoints;
import com.danielkashin.yandextestapplication.data_layer.entities.remote.Translation;
import com.danielkashin.yandextestapplication.data_layer.services.base.NetworkService;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;


public class YandexTranslateNetworkService extends NetworkService<IYandexTranslateNetworkContract>
    implements IYandexTranslateNetworkService {

  private YandexTranslateNetworkService(OkHttpClient okHttpClient){
    super(Endpoints.YANDEX_TRANSLATE_BASE_URL, okHttpClient);
  }

  @Override
  protected IYandexTranslateNetworkContract createService(Retrofit retrofit) {
    return retrofit.create(IYandexTranslateNetworkContract.class);
  }

  // -------------------------- IYandexTranslateNetworkService methods ----------------------------

  @Override
  public Call<Translation> translate(String text, String lang) {
    return getService().translate(BuildConfig.YANDEX_TRANSLATE_API_KEY, text, lang);
  }

  // -------------------------------------- Factory -----------------------------------------------

  public static final class Factory {

    private Factory() { }

    public static IYandexTranslateNetworkService create(OkHttpClient okHttpClient) {
      return new YandexTranslateNetworkService(okHttpClient);
    }
  }
}
