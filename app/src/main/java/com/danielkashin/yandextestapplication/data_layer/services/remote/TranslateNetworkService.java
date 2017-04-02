package com.danielkashin.yandextestapplication.data_layer.services.remote;

import com.danielkashin.yandextestapplication.BuildConfig;
import com.danielkashin.yandextestapplication.data_layer.constants.Endpoints;
import com.danielkashin.yandextestapplication.data_layer.entities.remote.NetworkTranslation;
import com.danielkashin.yandextestapplication.data_layer.services.base.NetworkService;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;


public class TranslateNetworkService extends NetworkService<ITranslateNetworkContract>
    implements ITranslateNetworkService {

  private TranslateNetworkService(OkHttpClient okHttpClient){
    super(Endpoints.YANDEX_TRANSLATE_BASE_URL, okHttpClient);
  }

  @Override
  protected ITranslateNetworkContract createService(Retrofit retrofit) {
    return retrofit.create(ITranslateNetworkContract.class);
  }

  // -------------------------- ITranslateNetworkService methods ----------------------------

  @Override
  public Call<NetworkTranslation> translate(String text, String lang) {
    return getService().translate(BuildConfig.YANDEX_TRANSLATE_API_KEY, text, lang);
  }

  // -------------------------------------- Factory -----------------------------------------------

  public static final class Factory {

    private Factory() { }

    public static ITranslateNetworkService create(OkHttpClient okHttpClient) {
      return new TranslateNetworkService(okHttpClient);
    }
  }
}
