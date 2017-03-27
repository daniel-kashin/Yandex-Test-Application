package com.danielkashin.yandextestapplication.data_layer.services.yandex_translate;


import com.danielkashin.yandextestapplication.BuildConfig;
import com.danielkashin.yandextestapplication.data_layer.constants.Endpoints;
import com.danielkashin.yandextestapplication.data_layer.services.base.NetworkService;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Query;

public class YandexTranslateNetworkService extends NetworkService<IYandexTranslateNetworkContract>
    implements IYandexTranslateNetworkService {

  private YandexTranslateNetworkService(OkHttpClient okHttpClient){
    super(Endpoints.YANDEX_TRANSLATE_BASE_URL, okHttpClient);
  }

  @Override
  protected IYandexTranslateNetworkContract createService(Retrofit retrofit) {
    return retrofit.create(IYandexTranslateNetworkContract.class);
  }

  // -------------------------- IYandexTranslateNetworkService methods -----------------------------

  @Override
  public Call<ResponseBody> getLangs(String ui) {
    return getService().getLangs(BuildConfig.YANDEX_TRANSLATE_API_KEY, ui);
  }

  @Override
  public Call<ResponseBody> translate(String text, String lang) {
    return getService().translate(BuildConfig.YANDEX_TRANSLATE_API_KEY, text, lang);
  }

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
