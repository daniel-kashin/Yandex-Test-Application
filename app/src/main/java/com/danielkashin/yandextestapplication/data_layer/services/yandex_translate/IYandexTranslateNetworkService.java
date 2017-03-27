package com.danielkashin.yandextestapplication.data_layer.services.yandex_translate;

import com.danielkashin.yandextestapplication.BuildConfig;

import okhttp3.ResponseBody;
import retrofit2.Call;

public interface IYandexTranslateNetworkService {

  public Call<ResponseBody> getLangs(String ui);

  public Call<ResponseBody> translate(String text, String lang);

}
