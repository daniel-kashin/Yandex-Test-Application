package com.danielkashin.yandextestapplication.data_layer.services.translate.remote;

import com.danielkashin.yandextestapplication.data_layer.entities.translate.remote.NetworkTranslation;
import com.danielkashin.yandextestapplication.data_layer.exceptions.ExceptionBundle;

import retrofit2.Call;

/*
* creating two interfaces and two classes for exactly one API call may seem funny but
* I think that worths it :)
*/
public interface ITranslationsRemoteService {

  Call<NetworkTranslation> translate(String text, String lang);

  void parseException(Exception exception) throws ExceptionBundle;

  void checkNetworkCodesForExceptions(int networkResponseCode) throws ExceptionBundle;
}
