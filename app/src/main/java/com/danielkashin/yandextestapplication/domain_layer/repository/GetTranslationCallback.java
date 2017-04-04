package com.danielkashin.yandextestapplication.domain_layer.repository;

import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;


public interface GetTranslationCallback {

  void onResult(Translation translation);

  void onError(Exception exception);

}
