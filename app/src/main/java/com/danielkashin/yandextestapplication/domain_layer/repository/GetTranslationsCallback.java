package com.danielkashin.yandextestapplication.domain_layer.repository;


import com.danielkashin.yandextestapplication.domain_layer.pojo.Translation;

import java.util.List;

public interface GetTranslationsCallback {

  void onResult(List<Translation> translation);

  void onError(Exception exception);

}
