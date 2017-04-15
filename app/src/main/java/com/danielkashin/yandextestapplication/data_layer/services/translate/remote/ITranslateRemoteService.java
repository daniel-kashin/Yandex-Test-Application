package com.danielkashin.yandextestapplication.data_layer.services.translate.remote;

import com.danielkashin.yandextestapplication.data_layer.entities.translate.remote.NetworkTranslation;
import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;

import retrofit2.Call;

public interface ITranslateRemoteService {

  Call<NetworkTranslation> translate(String text, String lang);

  void tryToThrowExceptionBundle(Exception exception) throws ExceptionBundle;

  void tryToThrowExceptionBundle(int networkResponseCode) throws ExceptionBundle;
}