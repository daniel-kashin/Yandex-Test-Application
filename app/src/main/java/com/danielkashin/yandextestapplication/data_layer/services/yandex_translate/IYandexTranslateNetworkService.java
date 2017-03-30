package com.danielkashin.yandextestapplication.data_layer.services.yandex_translate;

import com.danielkashin.yandextestapplication.BuildConfig;
import com.danielkashin.yandextestapplication.data_layer.entitles.yandex_translate.Translation;

import okhttp3.ResponseBody;
import retrofit2.Call;

public interface IYandexTranslateNetworkService {

  public Call<ResponseBody> getLangs(String ui);

  public Call<Translation> translate(String text, String lang);

}
