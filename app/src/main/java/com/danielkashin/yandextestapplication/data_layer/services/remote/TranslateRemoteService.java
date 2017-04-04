package com.danielkashin.yandextestapplication.data_layer.services.remote;

import com.danielkashin.yandextestapplication.BuildConfig;
import com.danielkashin.yandextestapplication.data_layer.constants.Endpoints;
import com.danielkashin.yandextestapplication.data_layer.entities.remote.NetworkTranslation;
import com.danielkashin.yandextestapplication.data_layer.services.base.NetworkService;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;


public class TranslateRemoteService extends NetworkService<ITranslateRemoteContract>
    implements ITranslateRemoteService {

  private TranslateRemoteService(OkHttpClient okHttpClient){
    super(Endpoints.YANDEX_TRANSLATE_BASE_URL, okHttpClient);
  }

  @Override
  protected ITranslateRemoteContract createService(Retrofit retrofit) {
    return retrofit.create(ITranslateRemoteContract.class);
  }

  // -------------------------- ITranslateRemoteService methods ----------------------------

  @Override
  public Call<NetworkTranslation> translate(String text, String lang) {
    return getService().translate(BuildConfig.YANDEX_TRANSLATE_API_KEY, text, lang);
  }

  // -------------------------------------- Factory -----------------------------------------------

  public static final class Factory {

    private Factory() { }

    public static ITranslateRemoteService create(OkHttpClient okHttpClient) {
      return new TranslateRemoteService(okHttpClient);
    }
  }
}
