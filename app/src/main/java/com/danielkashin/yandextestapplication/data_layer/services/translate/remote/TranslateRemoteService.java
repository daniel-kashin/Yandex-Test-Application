package com.danielkashin.yandextestapplication.data_layer.services.translate.remote;

import com.danielkashin.yandextestapplication.BuildConfig;
import com.danielkashin.yandextestapplication.data_layer.constants.Endpoints;
import com.danielkashin.yandextestapplication.data_layer.contracts.translate.remote.ITranslationRemoteContract;
import com.danielkashin.yandextestapplication.data_layer.entities.translate.remote.NetworkTranslation;
import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;
import com.danielkashin.yandextestapplication.data_layer.services.base.NetworkService;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLException;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;


public class TranslateRemoteService extends NetworkService<ITranslationRemoteContract>
    implements ITranslateRemoteService {

  private TranslateRemoteService(OkHttpClient okHttpClient) {
    super(Endpoints.YANDEX_TRANSLATE_BASE_URL, okHttpClient);
  }

  @Override
  protected ITranslationRemoteContract createService(Retrofit retrofit) {
    return retrofit.create(ITranslationRemoteContract.class);
  }

  // -------------------------- ITranslateRemoteService methods ----------------------------

  @Override
  public Call<NetworkTranslation> translate(String text, String lang) {
    return getService().translate(BuildConfig.YANDEX_TRANSLATE_API_KEY, text, lang);
  }

  @Override
  public void tryToThrowExceptionBundle(Exception exception) throws ExceptionBundle {
    if (exception instanceof ExceptionBundle) {
      throw (ExceptionBundle) exception;
    } else if (exception instanceof ConnectException || exception instanceof SocketTimeoutException
        || exception instanceof UnknownHostException || exception instanceof SSLException) {
      throw new ExceptionBundle(ExceptionBundle.Reason.NETWORK_UNAVAILABLE);
    } else {
      throw new ExceptionBundle(ExceptionBundle.Reason.UNKNOWN);
    }
  }

  @Override
  public void tryToThrowExceptionBundle(int networkResponseCode) throws ExceptionBundle {
    if (networkResponseCode == 200) {
      // do nothing, everything is alright
    } else if (networkResponseCode == 401) {
      throw new ExceptionBundle(ExceptionBundle.Reason.WRONG_KEY);
    } else if (networkResponseCode == 404) {
      throw new ExceptionBundle(ExceptionBundle.Reason.LIMIT_EXPIRED);
    } else if (networkResponseCode == 413 || networkResponseCode == 414) {
      throw new ExceptionBundle(ExceptionBundle.Reason.TEXT_LIMIT_EXPIRED);
    } else if (networkResponseCode == 422) {
      throw new ExceptionBundle(ExceptionBundle.Reason.WRONG_TEXT);
    } else if (networkResponseCode == 501) {
      throw new ExceptionBundle(ExceptionBundle.Reason.WRONG_LANGS);
    } else {
      throw new ExceptionBundle(ExceptionBundle.Reason.UNKNOWN);
    }
  }

  // -------------------------------------- Factory -----------------------------------------------

  public static final class Factory {

    private Factory() {
    }

    public static ITranslateRemoteService create(OkHttpClient okHttpClient) {
      return new TranslateRemoteService(okHttpClient);
    }
  }
}
